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
package my.ep.web.backingbean.sc.committee;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.criteria.JoinType;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
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
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
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
import my.ep.persistence.entity.sc.quationtender.ScCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.committee.CommitteManagedBean;
import my.ep.web.type.sc.quationtender.MemberType;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * The Class CommitteBackingBean.
 *
 * @author VidyasagarReddy
 */
@ManagedBean(name = "committeBackingBean")
@RequestScoped
public class CommitteBackingBean extends
		SourcingBaseBackingBean<CommitteManagedBean> {

	/** The Constant NAV_TO_COMMITTEE_MAINTENANACE. */
	private static final String NAV_TO_COMMITTEE_MAINTENANACE = "/xhtml/sc/committe/committeeMaintanancePage.xhtml";
	
	/** The Constant NAV_TO_VIEW_HISTORY. */
	private static final String NAV_TO_VIEW_HISTORY = "/xhtml/sc/committe//history/viewHistory.xhtml";
	
	/** The Constant NAV_TO_SEARCH_PAGE. */
	private static final String NAV_TO_SEARCH_PAGE = "/xhtml/sc/committe/committeeSearch.xhtml";

	/** The Constant logger. */
	private static final Logger logger = EpLogger
			.getLogger(CommitteBackingBean.class);

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	/**
	 * Instantiates a new committe backing bean.
	 */
	public CommitteBackingBean() {
		getManagedBean().setRoleTypes(getRoleTypeDropdownValues());

		/*if (this.getManagedBean().getOutComes() == null) {
			this.getManagedBean().setOutComes(
					new String[] { BpmOutcomeConst.BPM_SAVE,
							BpmOutcomeConst.BPM_SUBMIT });
		}*/

		/*
		 * IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
		 * .getCloneSearchQTDataModel(); if (cloneSearchQTDataModel == null) {
		 * this.getManagedBean().setCloneSearchQTDataModel( new
		 * CloneQTPagingDataModel("clonesearchCommitteid",
		 * getManagedBean().getCloneQtnTenderSearchBean())); }
		 * 
		 * IPagingDataModel<PmUser> userSearhQCDataModel = getManagedBean()
		 * .getUserSearchQCDataModel(); if (userSearhQCDataModel == null) {
		 * this.getManagedBean().setUserSearchQCDataModel( new
		 * UserQCPagingDataModel("userexistid", getManagedBean()
		 * .getUserQCSearchBean())); }
		 */
	}

	/**
	 * The Class UserQCPagingDataModel.
	 */
	private class UserQCPagingDataModel extends PagingDataModel<PmUser> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The user qc search bean. */
		private UserCommitteeSearchBean userQCSearchBean;

		/**
		 * Instantiates a new user qc paging data model.
		 *
		 * @param componentId the component id
		 * @param userQCSearchBean the user qc search bean
		 */
		public UserQCPagingDataModel(String componentId,
				UserCommitteeSearchBean userQCSearchBean) {
			super(componentId,false);
			this.userQCSearchBean = userQCSearchBean;
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
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
				populateUserSearchCriteria(userQCSearchBean, searchBean);
				userReqDTO.setUserSearchBean(searchBean);
				UserResDTO resDto = userSO.searchUserProfile(userReqDTO);
				if (resDto != null) {
					getManagedBean().setExternalUsers(
							resDto.getUserResultBean().getResultList());
					for (PmUser user : getManagedBean().getExternalUsers()) {
						if (user.getMemberRole() == null) {
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

	/**
	 * Populate user search criteria.
	 *
	 * @param userQCSearchBean the user qc search bean
	 * @param userSearchBean the user search bean
	 */
	private void populateUserSearchCriteria(
			UserCommitteeSearchBean userQCSearchBean,
			ISearchBean<PmUser> userSearchBean) {
		if (userQCSearchBean != null) {

			String name = userQCSearchBean.getName();
			if (!StringUtils.isBlank(name)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME,
								SearchOperator.OPERATOR_LIKE.value(), name,
								true));
			}
			String idno = userQCSearchBean.getIdentificationNo();
			if (!StringUtils.isBlank(idno)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.IDENTIFICATION_NO,
								SearchOperator.OPERATOR_LIKE.value(), idno,
								true));
			}
			String idtype = userQCSearchBean.getIdentificationType();
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
							DbValueConst.RECORD_STATUS_ACTIVE)					
					);	
			
			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE)					
					);				
			
			/* Commented as part of CR change to fetch irrespective of Ministry.
			 *   
			 * if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
				Long ministryOrgProfile = retrieveMinistryOrgProfile(
						getManagedBean().getQt().getPmOrgProfile().getOrgProfileId()).getOrgProfileId();
				String committeeType = getManagedBean().getScCommittee().getCommitteeType().getParameterCode();
				SearchFilterBean ptjOrgProfileFB = new SearchFilterBean(
						"pmUserOrgs.pmOrgProfile."
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
						SearchOperator.OPERATOR_EQUALS.value(),
						ministryOrgProfile,
						JoinType.LEFT);
				
				if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equals(committeeType) ||
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equals(committeeType)) {
					List<String> orgTypes = new ArrayList<String>();
					orgTypes.add(ParameterConst.ORG_TYPE_SM_PMS_SABAH);
					orgTypes.add(ParameterConst.ORG_TYPE_SM_PMS_SARAWAK);
					orgTypes.add(ParameterConst.ORG_TYPE_OTHERS);
					
					SearchFilterBean otherOrgFB = new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							orgTypes,
							SearchCondition.CONDITION_OR.value());
					userSearchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(ptjOrgProfileFB, otherOrgFB));
					
				} else {
					SearchFilterBean otherOrgFB = new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							ParameterConst.ORG_TYPE_OTHERS,
							SearchCondition.CONDITION_OR.value());
					
					if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equals(committeeType) ||
							ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equals(committeeType)) {
						SearchFilterBean ministryOrgProfileFB = new SearchFilterBean(
								"pmUserOrgs.pmOrgProfile.orgProfileId",
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryOrgProfile);
						userSearchBean.getDataFilterBeans().add(
								new SearchMultiFilterBean(ptjOrgProfileFB, 
										new SearchMultiFilterBean(ministryOrgProfileFB, SearchCondition.CONDITION_OR.value(), otherOrgFB)));
					} else {
						userSearchBean.getDataFilterBeans().add(
								new SearchMultiFilterBean(ptjOrgProfileFB, otherOrgFB));
					}
				} 
			}*/
		}
	}

	/**
	 * Search user action.
	 */
	public void searchUserAction() {
		this.getManagedBean().getUserSearchQCDataModel().reloadData();
	}
	
	/**
	 * Get user role description
	 */
	public String getRoleDesc(PmUser pmUser) {
		String userRole = null;
		String orgType = null;
		String committeeType = getManagedBean().getQtCommittee()
				.getScCommittee().getCommitteeType().getParameterCode();
		StringBuffer chairPersonRoleCode = new StringBuffer(
				committeeType + QTConstants.CHAIRPERSON);
		
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			userSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			
			SearchBean<PmUserRole> searchBean = new SearchBean<PmUserRole>();

			// Filter By - user org info
			// User id
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrg.pmUser.userId", 
							SearchOperator.OPERATOR_EQUALS.value(), 
							pmUser.getUserId()));
			
			// User Org type
			if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equals(committeeType) || 
					ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK
							.equals(committeeType)) {
				orgType = null;

			} else if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equals(committeeType) || 
					ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equals(committeeType)) {
				orgType = ParameterConst.ORG_TYPE_PTJ;

			} else {
				orgType = ParameterConst.ORG_TYPE_MINISTRY;
			}
			if (orgType != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrg.pmUser.orgTypeCode.parameterCode", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								orgType));	
			}
			
			// Role Code
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmRole.roleCode", 
							SearchOperator.OPERATOR_EQUALS.value(), 
							chairPersonRoleCode));
			
			reqDTO.setUserRoleSearchBean(searchBean);
			UserResDTO resDTO = userSO.retrieveUserRoleBySearchBean(reqDTO);
			if (resDTO.isSuccessful() && 
					CollectionUtils.isNotEmpty(resDTO.getUserRoleList())) {
				PmParameter chairPersonRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
						ParameterTypeConst.COMMITTEE_ROLE, 
						ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
				pmUser.setMemberRole(chairPersonRole);
				userRole = getParameterDesc(chairPersonRole);
			}
			
		} finally {
			userSO = null;
		}
		
		if (userRole == null) {
			userRole = getParameterDesc(ParameterTypeConst.COMMITTEE_ROLE, 
					ParameterConst.COMMITTEE_ROLE_MEMBER);
		}
		
		return userRole;
	}

	/**
	 * Gets the role type dropdown values.
	 *
	 * @return the role type dropdown values
	 */
	public SelectItem[] getRoleTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init() {
		IPagingDataModel<ScQtCommittee> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();

		if (searchQTDataModel == null) {
			this.getManagedBean().setSearchQTDataModel(
					new QTPagingDataModel("searchQTs", getManagedBean()
							.getQtnTenderSearchBean()));
		}

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();
		if (cloneSearchQTDataModel == null) {
			this.getManagedBean().setCloneSearchQTDataModel(
					new CloneQTPagingDataModel("clonesearchCommitteid",
							getManagedBean().getCloneQtnTenderSearchBean()));
		}

		
	}
	
	private class committeeQTPagingDataModel extends PagingDataModel<ScQtCommittee> {
		
		private static final long serialVersionUID = 1L;

		private ScCommittee committee;
		
		/**
		 * @param componentId
		 * @param scCommittee
		 */
		public committeeQTPagingDataModel(String componentId,
				ScCommittee scCommittee) {
			super(componentId);
			this.committee = scCommittee;
		}
		
		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		protected ISearchResultBean<ScQtCommittee> pageChangeListener(
				ISearchBean<ScQtCommittee> committeeBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setScCommittee(committee);
				committeReqDTO.setCommSearchBean(committeeBean);
				CommitteResDTO resDTO = committeSO.retrieveCommitteDetailsByCommitteeType(committeReqDTO);
				return resDTO.getQtCommitteResultBean();
			} finally {
				committeSO = null;
			}
		}
		
		
	}

	/**
	 * The Class QTPagingDataModel.
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQtCommittee> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The qtn tender search bean. */
		private QtnTenderSearchBean qtnTenderSearchBean;

		/**
		 * Instantiates a new QT paging data model.
		 *
		 * @param componentId the component id
		 * @param qtnTenderSearchBean the qtn tender search bean
		 */
		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtCommittee> pageChangeListener(
				ISearchBean<ScQtCommittee> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				populateQTSearchCriteria(qtnTenderSearchBean, searchBean);
				committeReqDTO.setCommSearchBean(searchBean);
				CommitteResDTO resDTO = committeSO
						.searchQtCommittes(committeReqDTO);
				return resDTO.getQtCommitteResultBean();
			} finally {
				committeSO = null;
			}
		}
	}

	/**
	 * Populate qt search criteria.
	 *
	 * @param qtnTenderSearchBean the qtn tender search bean
	 * @param qtSearchBean the qt search bean
	 */
	private void populateQTSearchCriteria(
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScQtCommittee> qtSearchBean) {
		if (qtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQt.qtTitle",
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
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
			Date toCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			if (null != fromCreatedDate) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				
				Calendar c2 = Calendar.getInstance();
				c2.setTime(toCreatedDate);
				Calendar toDate = Calendar.getInstance();
				toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				toDate.set(Calendar.MILLISECOND, 999);
				toCreatedDate = toDate.getTime();
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				/*bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(fromCreatedDate);*/
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			/*String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scCommittee.committeStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
			}*/
			
			Long recordStatus = qtnTenderSearchBean.getRecordStatus();
			if (recordStatus != null && !"".equals(recordStatus)) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
				codesSearchBean.setSearchField("scCommittee.recordStatusFlg");
				codesSearchBean.setSearchValue(recordStatus);
				searchFilterBean.add(codesSearchBean);
			} else {
				SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
				codesSearchBean.setSearchField("scCommittee.recordStatusFlg");
				codesSearchBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
				searchFilterBean.add(codesSearchBean);
			}
			
			String committeeType = qtnTenderSearchBean.getCommitteeType();
			if (!StringUtils.isBlank(committeeType)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"scCommittee.committeeType.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(),
								committeeType));
			}
			
			// Filter by FC Committee only
			qtSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"scCommittee.committeeType.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							getFCCommitteeTypes()));
			
			// Filter by login user
			qtSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"scCommittee.scCommitteeMembers.pmUser.userId",
							SearchOperator.OPERATOR_EQUALS.value(),
							new FacesScopes().getUserSessionBean().getUserId(),
							JoinType.LEFT));
			
			// Filter by secretary role
			qtSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"scCommittee.scCommitteeMembers.memberRole.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							ParameterConst.COMMITTEE_ROLE_SECRETARY,
							JoinType.LEFT));
			
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			
			qtSearchBean.getSortColumns().add(new SearchOrderBean("qtCommitteeId", false));
		}
	}
	
	/**
	 * Populate FC committee type(s) in drop down
	 * 
	 * @return an array of FC committee type values
	 */
	public SelectItem[] getFCCommitteesDropDown() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(),
						getFCCommitteeTypes()));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_COMMITTEE_TYPE, searchBean);
	}
	
	private List<String> getFCCommitteeTypes() {
		List<String> committeeTypes = new ArrayList<String>();
		
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK);
		
		return committeeTypes;
	}

	/*
	 * public String invokeCommitteAction(String action) {
	 * 
	 * String page = null; if
	 * (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { page =
	 * saveCommitteDetails(); } else if
	 * (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) { page =
	 * submitCommitteDetails();
	 * 
	 * } else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) { page =
	 * updateCommitteBpmTask(action); } else if
	 * (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) { page =
	 * updateCommitteBpmTask(action); } FacesContext fc =
	 * FacesContext.getCurrentInstance();
	 * fc.getApplication().getNavigationHandler() .handleNavigation(fc, null,
	 * "/xhtml/shared/acknowledge.xhtml"); return page; }
	 * 
	 * private String updateCommitteBpmTask(String action) {
	 * 
	 * CommitteReqDTO committeReqDTO = new CommitteReqDTO(); //
	 * committeReqDTO.setQtCommittee(this.getManagedBean().getQtCommittee());
	 * FacesScopes facesScopes = new FacesScopes(); String ctxToken =
	 * facesScopes.getBPMAuthenticationCtxToken();
	 * committeReqDTO.setToken(ctxToken);
	 * committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
	 * committeReqDTO.setOutcome(action); ICommitteSO commso =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class); try {
	 * CommitteResDTO resDTO = commso.updateBPMAction(committeReqDTO); if
	 * (resDTO.isSuccessful()) { FacesContext.getCurrentInstance().addMessage(
	 * null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	 * "Record saved successfully.", "Committe Type. : " + this.getManagedBean()
	 * .getScCommittee() .getCommitteeType()));
	 * 
	 * return "/xhtml/shared/acknowledge.xhtml";
	 * 
	 * } } catch (Exception ex) { FacesContext.getCurrentInstance().addMessage(
	 * null, new FacesMessage(FacesMessage.SEVERITY_INFO,
	 * "Sorry For the inconvinence", "")); } return null;
	 * 
	 * }
	 */

	/**
	 * Method to save and submit the data to BPM.
	 *
	 * @param outCome the out come
	 * @return the string
	 */
	public String updateCommitteDetails(String outCome) {
		debugLogger("submitCommitteDetails");
		ScQtCommittee scQtCommittee = getManagedBean().getQtCommittee();
		
		CommitteReqDTO committeReqDTO = constructSaveCommitte();
		committeReqDTO.setOutcome(outCome);
		committeReqDTO.setScQtCommittee(scQtCommittee);
		
		CommitteResDTO resDTO = null;

		try {
			ICommitteSO committeSo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSo.updateCommitteDetails(committeReqDTO);

			if (resDTO.isSuccessful()) {

				if (null != outCome) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_700005,
															new String[] {
																	"Committe",
																	"" },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					// renewSesionObject();

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
																	"Committe",
																	"" },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

				}
			}
		} finally {

		}
		return null;
	}

	/**
	 * Construct save committe.
	 *
	 * @return the committe req dto
	 */
	private CommitteReqDTO constructSaveCommitte() {

		// ScQtcommitte date setting
		ScQtCommittee scQtCommittee = this.getManagedBean().getQtCommittee();

		// finalizeRemarks(scQtCommittee.getScCommittee().getScCommitteeRemarks());

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();

		committeReqDTO.setScQtCommittee(scQtCommittee);
		committeReqDTO.setToBeDeletedMembers(getManagedBean().getToBeDelCommitteMemebers());
		if (null != this.getManagedBean().getPayLoad()) {
			SCEvaluationDataType evalDataType = (SCEvaluationDataType) this
					.getManagedBean().getPayLoad();
			committeReqDTO.setQtId(Long.valueOf(evalDataType.getQtId()));
			committeReqDTO.setCommitteeType(evalDataType.getCommitteeType());
			committeReqDTO.setPayLoad(evalDataType);
			// committeReqDTO.setToken(this.getManagedBean().getTaskName());
			committeReqDTO.setTaskId(this.getManagedBean().getTaskId());

		} else {
			committeReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			committeReqDTO.setCommitteeType(this.getManagedBean()
					.getCommitteeType());
		}
		FacesScopes facesScopes = new FacesScopes();
		committeReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		return committeReqDTO;
	}

	/**
	 * Method to search the quationtenders based on serach date.
	 */
	public void searchQTAction() {
		getManagedBean().getSearchQTDataModel().reloadData();
	}

	/**
	 * Method to return the maintance page.
	 *
	 * @param scQtCommittee the sc qt committee
	 * @return maintanacepage
	 */
	public String editAction(ScQtCommittee scQtCommittee) {
		debugLogger("editAction");
		this.getManagedBean().setHeaderTitle(
				ParameterHelper.getInstance().getParameterDesc(
						ParameterTypeConst.SC_COMMITTEE_TYPE,
						scQtCommittee.getScCommittee().getCommitteeType().getParameterCode()));
		log.info("Title : " + getManagedBean().getHeaderTitle());
		String committeeType = scQtCommittee.getScCommittee()
				.getCommitteeType().getParameterCode();
		log.info("Committee Type : " + committeeType);
		if (committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)
				|| committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE)
				|| committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)
				|| committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee()
					.getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
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
					committee
							.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
				this.getManagedBean().setScCommittee(qtCommitte.getScCommittee());

				ScCommittee committee = resDTO.getCommittee();
				if (null == committee) {
					committee = new ScCommittee();
				}

				List<ScCommitteeRemark> committeeRemarks = initializeRemarks(
						committee, null, committee.getScCommitteeRemarks(),
						ScCommitteeRemark.class);

				committee.setScCommitteeRemarks(committeeRemarks);
				this.getManagedBean().setScCommitteeRemarksList(
						committeeRemarks);

			} finally {
			}
			showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";

		} else if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee()
					.getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
				qtCommitte = resDTO.getScQtCommittee();
				if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equalsIgnoreCase(committeeType)
						|| ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(committeeType)) {
					SelectItem[] qcItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
					this.getManagedBean().setRepresentativesTypeslist(
							Arrays.asList(qcItems));
				} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(committeeType)) {
					SelectItem[] pbbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
					this.getManagedBean().setRepresentativesTypeslist(
							Arrays.asList(pbbItems));
				} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equalsIgnoreCase(committeeType)) {
					SelectItem[] pbaItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBA);
					this.getManagedBean().setRepresentativesTypeslist(
							Arrays.asList(pbaItems));
				} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equalsIgnoreCase(committeeType)) {
					SelectItem[] fpbhItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH);
					this.getManagedBean().setRepresentativesTypeslist(
							Arrays.asList(fpbhItems));
				} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equalsIgnoreCase(committeeType)) {
					SelectItem[] fpbkItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK);
					this.getManagedBean().setRepresentativesTypeslist(
							Arrays.asList(fpbkItems));
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
					committee
							.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				for (ScCommitteeMember committeeMember : qtCommitte
						.getScCommittee().getScCommitteeMembers()) {
					if (committeeMember.getRepresentativeType() == null) {
						committeeMember
								.setRepresentativeType(new PmParameter());
					}
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
				this.getManagedBean().setScCommittee(qtCommitte.getScCommittee());
				
				this.getManagedBean().setCommitteeQTDataModel(
						new committeeQTPagingDataModel("qtDetailsId", getManagedBean().getScCommittee()));
			} finally {
			}
			showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";

		} else if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE)) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee()
					.getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
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
					committee
							.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
				this.getManagedBean().setScCommittee(qtCommitte.getScCommittee());
			} finally {
			}
			// showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";
		}
		return null;
	}

	/**
	 * View Committee history action.
	 *
	 * @param scQtCommittee the sc qt committee
	 * @return the string
	 */
	public String viewHistoryAction(ScQtCommittee scQtCommittee) {
		debugLogger("viewHistoryAction");
		ScCommittee scCommittee = scQtCommittee.getScCommittee();
		String committeeType = scCommittee.getCommitteeType().getParameterCode();
		log.info("Committee Type : " + committeeType);
		
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setCommitteeType(committeeType);
			
			PmUser fcSecretaryUser = null;
			for (ScCommitteeMember scCommitteeMember : scCommittee.getScCommitteeMembers()) {
				if (ParameterConst.COMMITTEE_ROLE_SECRETARY.equals(scCommitteeMember.getMemberRole().getParameterCode())) {
					fcSecretaryUser = scCommitteeMember.getPmUser();
				}
			}
			reqDTO.setFcSecretaryUser(fcSecretaryUser);
			
			CommitteResDTO resDTO = committeSO.retrieveFCCommitteHistoryDetails(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setHistoryCommittees(resDTO.getCommittes());
				this.getManagedBean().setSelectedHistoryCommittee(null);
			}			

		} finally {
			committeSO = null;
		}
		
		return NAV_TO_VIEW_HISTORY + "?faces-redirect=true";
	}
	
	/**
	 * View Committee History Details action.
	 *
	 */
	public void viewCommitteeHistoryDtlAction() {
		debugLogger("viewCommitteeHistoryDtlAction");
		ScCommittee selectedHistoryCommittee = this.getManagedBean().getSelectedHistoryCommittee();
		
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setScCommitteeId(selectedHistoryCommittee.getCommitteeId());
			
			CommitteResDTO resDTO = committeSO.retrieveCommitteDetailsByCommitteeId(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setSelectedHistoryCommittee(resDTO.getCommittee());
			}			

		} finally {
			committeSO = null;
		}
	}

	/**
	 * View history back action.
	 *
	 * @return the string
	 */
	public String viewHistoryBackAction() {
		debugLogger("viewHistoryBackAction");
		return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";
	}

	/**
	 * The Class CloneQTPagingDataModel.
	 */
	private class CloneQTPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The clone qtn tender search bean. */
		private QtnTenderSearchBean cloneQtnTenderSearchBean;

		/**
		 * Instantiates a new clone qt paging data model.
		 *
		 * @param componentId the component id
		 * @param cloneQtnTenderSearchBean the clone qtn tender search bean
		 */
		public CloneQTPagingDataModel(String componentId,
				QtnTenderSearchBean cloneQtnTenderSearchBean) {
			super(componentId);
			this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
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
	 * @param cloneQtnTenderSearchBean the clone qtn tender search bean
	 * @param qtSearchBean the qt search bean
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

			String procurementMode = cloneQtnTenderSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField("procurementMode.parameterCode");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(procurementMode);
				searchFilterBean.add(bean);
			}

			Date fromCreatedDate = cloneQtnTenderSearchBean
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

	/**
	 * Clone search qt action.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	/**
	 * On row clone action.
	 *
	 * @param event the event
	 */
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
				
				this.getManagedBean().setCommTypeTitle(
						ParameterHelper.getInstance().getParameterDesc(
								ParameterTypeConst.SC_COMMITTEE_TYPE,
								committeeMember.getScCommittee().getCommitteeType().getParameterCode()));
				logger.info("Committee Type : "
						+ this.getManagedBean().getCommTypeTitle());
			}
			this.getManagedBean().setCloneCommitteMemebers(commMembers);
			
		} finally{
			
		}
	}

	/**
	 * Method to show the qtdetails.
	 *
	 * @param qtId the qt id
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
		/*
		 * System.out.println(" Status : " +
		 * this.getManagedBean().getQt().getScQtCommittees
		 * ().get(0).getScCommittee().getCommitteStatus().getStatusId());
		 */
		System.out.println("ProcurementMode : "
				+ this.getManagedBean().getQt().getProcurementMode());
	}

	/**
	 * Method to provide nessasary info to commitperson.
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
	
	/**
	 * Event to handle selection of committee members
	 */
	public void selectAllMembers(boolean selectAll) {
		debugLogger("addToSelectedMemberList");
		
		List<ScCommitteeMember> committeeMemberList = 
				this.getManagedBean().getSelectedCommitteMemebers();
		
		if (CollectionUtils.isNotEmpty(committeeMemberList)) {
			selectAll(selectAll, committeeMemberList);
			if (this.getManagedBean()
					.getSelectedCommitteMemebers() == null) {
				this.getManagedBean().setSelectedCommitteMemebers(
						new ArrayList<ScCommitteeMember>());
			} 
			if (selectAll) {
				this.getManagedBean()
					.getSelectedCommitteMemebers().clear();
				this.getManagedBean()
					.getSelectedCommitteMemebers()
					.addAll(committeeMemberList);
			} else {
				this.getManagedBean()
					.setSelectedCommitteMemebers(null);
			}
		}
	}
	
	/**
	 * Event to handle selection of committee members
	 */
	public void selectMemberEvent(SelectEvent event) {
		debugLogger("selectMemberEvent");
		
		ScCommitteeMember committeeMember = 
								(ScCommitteeMember) event.getObject();
		if (committeeMember.getMemberRole().getParameterCode()
				.equals(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
			committeeMember.setSelected(true);
			selectMemberEvent(committeeMember);
		} else {
			return;
		}
	}
	
	public void selectMemberEvent(ScCommitteeMember committeeMember) {
		this.getManagedBean().setSelectAllMembers(false);
		List<ScCommitteeMember> selectedCommitteeMembers 
				= this.getManagedBean().getSelectedCommitteMemebers();
		if (selectedCommitteeMembers == null) {
			selectedCommitteeMembers = new ArrayList<ScCommitteeMember>();
		}
		if (committeeMember.isSelected() && 
				!selectedCommitteeMembers.contains(committeeMember)) {
			selectedCommitteeMembers.add(committeeMember);	
		} else {
			selectedCommitteeMembers.remove(committeeMember);
		}
		this.getManagedBean().setSelectedCommitteMemebers(
				selectedCommitteeMembers);
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

	/**
	 * Adds the committe member.
	 *
	 * @param titleName the title name
	 */
	public void addCommitteMember(String titleName) {
		debugLogger("addCommitteMember");
		IPagingDataModel<PmUser> userSearhQCDataModel = getManagedBean()
				.getUserSearchQCDataModel();
		if (userSearhQCDataModel == null) {
			this.getManagedBean().setUserSearchQCDataModel(
					new UserQCPagingDataModel("userexistid", getManagedBean()
							.getUserQCSearchBean()));
		}
		
		UserCommitteeSearchBean userQCSearchBean 
									= getManagedBean().getUserQCSearchBean();		
		userQCSearchBean.setName(null);
		userQCSearchBean.setIdentificationNo(null);
		userQCSearchBean.setIdentificationType(null);
		
		getManagedBean().getUserSearchQCDataModel().resetData();
		// getManagedBean().getUserQCSearchBean().setName(null);
		// getManagedBean().getUserQCSearchBean().setIdentificationType(null);
		// getManagedBean().getUserQCSearchBean().setIdentificationNo(null);
		// getManagedBean().setExternalUsers(null);
		// getManagedBean().setSelectedPmUser(null);
		this.getManagedBean().setCommitteMember(null);
		this.getManagedBean().setSelectedPmUser(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(QTConstants.USER_SELECTION);
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		// getManagedBean().setQtCommittee(qtCommittee);
		getManagedBean().setHeaderTitle(titleName);
		String commType = getManagedBean().getQtCommittee().getScCommittee()
				.getCommitteeType().getParameterCode();
		if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(commType)) {
			SelectItem[] pbbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
			this.getManagedBean().setAddRepresentativesTypeslist(
					Arrays.asList(pbbItems));
		} else if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(commType)) {
			SelectItem[] qcaItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
			this.getManagedBean().setAddRepresentativesTypeslist(
					Arrays.asList(qcaItems));
		}
		getManagedBean().setAutoUpdate(Boolean.FALSE);
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
	 * @param event the event
	 */
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
			getManagedBean().getCommitteMember().setRepresentativeType(
					new PmParameter());
		}
	}

	/**
	 * Delete committee action.
	 */
	public void deleteCommitteeAction() {
		debugLogger("deleteCommitteeAction");
		List<ScCommitteeMember> toBeDelCommitteMemebers = getManagedBean().getToBeDelCommitteMemebers();
		this.getManagedBean().setAutoUpdate(Boolean.TRUE);
		List<ScCommitteeMember> committeeMemberList = this.getManagedBean()
				.getQtCommittee().getScCommittee().getScCommitteeMembers();
		List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getSelectedCommitteMemebers();
		if (CollectionUtils.isEmpty(selectedList)) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		
		if (toBeDelCommitteMemebers == null) {
			toBeDelCommitteMemebers = new ArrayList<ScCommitteeMember>();
			this.getManagedBean().setToBeDelCommitteMemebers(toBeDelCommitteMemebers);	
		}
		
		for (int i = 0; i < selectedList.size(); i++) {
			committeeMemberList.remove(selectedList.get(i));
			selectedList.get(i).setDeleted(true);
		}
		this.getManagedBean().setSelectedCommitteMemebers(null);
		
		toBeDelCommitteMemebers.addAll(selectedList);
	}

	/**
	 * Existing committee search action.
	 */
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
		
		SearchFilterBean recordStatus = new SearchFilterBean(
				SearchOperator.OPERATOR_EQUALS.value());
		recordStatus.setSearchField("recordStatusFlg");
		recordStatus.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(recordStatus);
		
		userReqDTO.setUserSearchBean(searchBean);
		
		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if (resDto != null) {
				this.getManagedBean().setExternalUsers(
						resDto.getUserResultBean().getResultList());

				for (PmUser user : this.getManagedBean().getExternalUsers()) {
					if (user.getMemberRole() == null) {
						user.setMemberRole(new PmParameter());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Reset.
	 */
	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserQCSearchBean().setName(null);
		this.getManagedBean().getUserQCSearchBean().setIdentificationType(null);
		this.getManagedBean().getUserQCSearchBean().setIdentificationNo(null);
	}

	/**
	 * Ok committee action.
	 */
	public void okCommitteeAction() {
		debugLogger("okCommitteeAction");
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = this.getManagedBean().getQtCommittee()
				.getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = getManagedBean().getQtCommittee()
				.getScCommittee().getScCommitteeMembers();
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
			PmRole pmRole = new PmRole();
			pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
			pmUserRole.setPmRole(pmRole);
			pmUserRoles.add(pmUserRole);
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
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);
			member.setPmUser(resDTO.getPmUser());
			setUserDetails(member);
			memberList.add(member);

		} else {
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
					if (users.get(i).isPMP() == true) {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
						reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
						ParameterResDTO resDTO = getParameterSO()
								.retrieveParameterByTypeAndCode(reqDTO);
						committeeMember.setMemberRole(resDTO.getPmParameter());
					} else {
						if (null != users.get(i).getMemberRole()
								.getParameterId()) {
							ParameterReqDTO reqDTO = new ParameterReqDTO();
							reqDTO.setParameterId(users.get(i).getMemberRole()
									.getParameterId());
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterById(reqDTO);
							committeeMember.setMemberRole(resDTO
									.getPmParameter());
						} else {
							committeeMember.setMemberRole(new PmParameter());
						}
					}
					committeeMember.setAccessRight(QTConstants.ACCESS_RIGHT);
					committeeMember.setScCommittee(scCommittee);
					setUserDetails(committeeMember);
					memberList.add(committeeMember);
				}
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}
		}
		if (isMemberExists(scCommittee.getScCommitteeMembers(), memberList)) {
			throw new EpException(MessageConst.ERROR_760048,
					new String[] { 
						ParameterHelper.getInstance().getParameterDesc(
								ParameterTypeConst.SC_COMMITTEE_TYPE,
								scCommittee.getCommitteeType().getParameterCode()) });

		}
		sortMemberList(memberList);
		this.getManagedBean().setScCommitteeMemberList(memberList);

		scCommittee.setScCommitteeMembers(memberList);
		this.getManagedBean().getQtCommittee().setScCommittee(scCommittee);
	}

	/**
	 * Checks if is member exists.
	 *
	 * @param existingMembers the existing members
	 * @param newMembers the new members
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
	 * Sort member list.
	 *
	 * @param memberList the member list
	 */
	public void sortMemberList(List<ScCommitteeMember> memberList) {
		Comparator<ScCommitteeMember> comparator = new Comparator<ScCommitteeMember>() {
			@Override
			public int compare(ScCommitteeMember o1, ScCommitteeMember o2) {

				String memberRole1 = o1.getMemberRole().getParameterCode();
				Integer memberRole1Int = roleValue(memberRole1);

				String memberRole2 = o2.getMemberRole().getParameterCode();
				Integer memberRole2Int = roleValue(memberRole2);

				if (memberRole1Int < memberRole2Int)
					return -1;// Return a negative value if object1 is smaller
								// than object2
				if (memberRole1Int == memberRole2Int)
					return 0;// Return 0 (zero) if objec1 is equal to object2.
				return 1; // Return a positive value if object1 is larger than
							// object2.
			}

			private int roleValue(String memberRole) {
				Integer value = null;

				if (memberRole == null) {
					value = 4;
				} else {
					if (memberRole
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
						value = 1;
					} else if (memberRole
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
						value = 2;
					} else if (memberRole
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
						value = 3;
					}
				}
				return value;
			}
		};
		Collections.sort(memberList, comparator);
	}

	/**
	 * Ok qc committee action.
	 */
	public void okQCCommitteeAction() {
		debugLogger("okCommitteeAction");
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = this.getManagedBean().getQtCommittee()
				.getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = getManagedBean().getQtCommittee()
				.getScCommittee().getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		List<ScCommitteeMember> tobeAddedList = new ArrayList<ScCommitteeMember>();
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
			member.setRepresentativeType(getManagedBean().getCommitteMember()
					.getRepresentativeType());
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
			PmRole pmRole = new PmRole();
			pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
			pmUserRole.setPmRole(pmRole);
			pmUserRoles.add(pmUserRole);
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
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);
			member.setPmUser(resDTO.getPmUser());
			setUserDetails(member);
			// memberList.add(member);
			tobeAddedList.add(member);
			// this.getManagedBean().setScCommitteeMemberList(tobeAddedList);
		} else {
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
	
					PmParameter roleParaMeter = ParameterHelper.getInstance()
							.getParameterByTypeAndCode(
									ParameterTypeConst.COMMITTEE_ROLE,
									ParameterConst.COMMITTEE_ROLE_MEMBER);
					committeeMember.setMemberRole(roleParaMeter);
					
					if (user.getMemberRole() == null || user.getMemberRole().getParameterId() == null) {
						PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
								ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
						user.setMemberRole(memberRole);
					} 
					committeeMember.setMemberRole(user.getMemberRole());					
	
					committeeMember.setAccessRight("E");
					if (ParameterConst.COMMITTEE_ROLE_MEMBER
							.equals(user.getMemberRole().getParameterCode())) {
						committeeMember.setRepresentativeType(new PmParameter());
					} else {
						committeeMember.setRepresentativeType(null);						
					}
					committeeMember.setScCommittee(scCommittee);
					// memberList.add(committeeMember);
					tobeAddedList.add(committeeMember);
				}
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}

		}
		if (isMemberExists(scCommittee.getScCommitteeMembers(), tobeAddedList)) {
			throw new EpException(MessageConst.ERROR_760048,
					new String[] { 
						ParameterHelper.getInstance().getParameterDesc(
								ParameterTypeConst.SC_COMMITTEE_TYPE,
								scCommittee.getCommitteeType().getParameterCode()) });

		}
		memberList.addAll(tobeAddedList);

		this.getManagedBean().setCommitteMembers(memberList);
		scCommittee.setScCommitteeMembers(memberList);
		this.getManagedBean().getQtCommittee().setScCommittee(scCommittee);
		RequestContext.getCurrentInstance().execute("screen3.hide()");
	}

	/**
	 * User type lie gov or non gov users.
	 *
	 * @return the user types
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
	 * Clone committe members acition.
	 *
	 * @param titleName the title name
	 */
	public void cloneCommitteMembersAcition(String titleName) {
		debugLogger("cloneCommitteMembersAcition");
		this.getManagedBean().setHeaderTitle(titleName);
		System.out.println("Title Name : "
				+ this.getManagedBean().getHeaderTitle());
	}

	/**
	 * Ok clone committee members action.
	 */
	public void okCloneCommitteeMembersAction() {
		debugLogger("okCloneCommitteeMembersAction");
		ScCommittee scCommittee = this.getManagedBean().getQtCommittee()
				.getScCommittee();
		List<ScCommitteeMember> memberList = getManagedBean().getQtCommittee()
				.getScCommittee().getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		List<String> itemIds = new ArrayList<String>();
		List<ScCommitteeMember> members = getManagedBean()
				.getSelectedCommitteMemebers();
		if (getManagedBean().getQtCommittee().getScCommittee()
				.getScCommitteeMembers().isEmpty()) {
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
			// memberList.addAll(getManagedBean().getSelectedCloneCommitteMemebers());
		} else {
			for (ScCommitteeMember committee : this.getManagedBean()
					.getQtCommittee().getScCommittee().getScCommitteeMembers()) {
				itemIds.add(committee.getIcPassport());
			}
			for (ScCommitteeMember committees : getManagedBean()
					.getSelectedCloneCommitteMemebers()) {
				if (itemIds.size() > 0) {
					if (itemIds.contains(committees.getIcPassport())) {

					} else {
						committees.setCommitteeMemberId(null);
						setUserDetails(committees);
						committees.setChangedDate(null);
						memberList.add(committees);
						this.getManagedBean().getQtCommittee().getScCommittee()
								.setScCommitteeMembers(memberList);
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

	/**
	 * Retrive user details.
	 *
	 * @param userid the userid
	 */
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

	/**
	 * Retrive all minstrys.
	 */
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

	/**
	 * Reset committe details.
	 */
	public void resetCommitteDetails() {
		resetCommitte();
	}

	/**
	 * Reset committe.
	 */
	private void resetCommitte() {
		getManagedBean().setCloneCommitteMemebers(null);
		getManagedBean().setCloneSearchCommittes(null);
		getManagedBean().setCommitteMembers(null);
	}

	/**
	 * On row edit action.
	 *
	 * @param event the event
	 */
	public void onRowEditAction(RowEditEvent event) {
		ScCommitteeMember committeeMember = (ScCommitteeMember) event
				.getObject();
		getManagedBean().setCommitteMember(committeeMember);

	}

	/**
	 * On row cancel.
	 *
	 * @param event the event
	 */
	public void onRowCancel(RowEditEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		this.getManagedBean().getCommitteMembers().remove(member);
	}

	/**
	 * Cancel committe action.
	 */
	public void cancelCommitteAction() {
		this.getManagedBean().getCommitteMembers()
				.remove(this.getManagedBean().getCommitteMember());
	}

	/*
	 * public void goToMaintainceAction(ScQtCommittee qtCommitte) {
	 * ScQtCommittee committee = qtCommitte; CommitteReqDTO committeReqDTO = new
	 * CommitteReqDTO(); //
	 * committeReqDTO.setCommitteId(committee.getQtCommitteeId()); //
	 * committeReqDTO.setQtCommittee(committee); ICommitteSO committeSO =
	 * BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	 * 
	 * CommitteResDTO resDTO = committeSO
	 * .retriveQTCommitteDetails(committeReqDTO);
	 * 
	 * // this.getManagedBean().setQtCommittee(resDTO.getCommittee());
	 * 
	 * this.getManagedBean().setScCommittee(
	 * resDTO.getCommittee().getScCommittee());
	 * 
	 * 
	 * this.getManagedBean().setCommittemembers(
	 * resDTO.getCommittee().getScCommittee().getScCommitteeMembers());
	 * this.getManagedBean().setApprovers(resDTO.getCommitteApprovers());
	 * 
	 * this.getManagedBean().setCommitteMember(new ScCommitteeMember());
	 * ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler)
	 * FacesContext
	 * .getCurrentInstance().getApplication().getNavigationHandler();
	 * handler.performNavigation("committeemaintanancepage");
	 * 
	 * }
	 */

	/* (non-Javadoc)
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#preRenderView()
	 */
	public void preRenderView() {
		Object payLoad = getBpmTaskDetails(Object.class, null);
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		if (SCProcessNameConst.ASSIGN_FC_MEMBER.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {

			SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) payLoad;
			this.getManagedBean().setPayLoad(evaluationDataType);
			committeReqDTO.setQtId(Long.valueOf(evaluationDataType.getQtId()));
			committeReqDTO.setCommitteeType(evaluationDataType
					.getCommitteeType());
			// committeReqDTO.setCommitteeType("QCA");
			CommitteResDTO committeResDTO = committeSO
					.retrieveCommitteByQtIdNCommType(committeReqDTO);
			ScQtCommittee qtCommittee = committeResDTO.getScQtCommittee();
			this.getManagedBean().setQtCommittee(qtCommittee);
			String committeeType = qtCommittee.getScCommittee()
					.getCommitteeType().getParameterCode();
			this.getManagedBean().setHeaderTitle(
					ParameterHelper.getInstance().getParameterDesc(
							ParameterTypeConst.SC_COMMITTEE_TYPE,
							qtCommittee.getScCommittee().getCommitteeType().getParameterCode()));
			this.getManagedBean().setScCommittee(qtCommittee.getScCommittee());
			this.getManagedBean().setQt(qtCommittee.getScQt());
			String representitiveType = null;
			if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equalsIgnoreCase(committeeType)
					|| ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(committeeType)) {
				representitiveType = ParameterTypeConst.REPRESENTATIVE_TYPE_QC;
			} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(committeeType)) {
				representitiveType = ParameterTypeConst.REPRESENTATIVE_TYPE_PBB;
			} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equalsIgnoreCase(committeeType)) {
				representitiveType = ParameterTypeConst.REPRESENTATIVE_TYPE_PBA;
			} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equalsIgnoreCase(committeeType)) {
				representitiveType = ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH;
			} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equalsIgnoreCase(committeeType)) {
				representitiveType = ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK;
			}
			SelectItem[] pbaItems = getDropdownValues(representitiveType);
			this.getManagedBean().setRepresentativesTypeslist(
					Arrays.asList(pbaItems));
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true");
	}

	/**
	 * Reset search details.
	 */
	public void resetSearchDetails() {
		this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setRecordStatus(null);
		this.getManagedBean().getQtnTenderSearchBean().setCommitteeType(null);
	}

	/**
	 * Reset clone.
	 */
	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setQtNo(null);
		this.getManagedBean().setProcurementMode(null);
		this.getManagedBean().setCreatedDtFrom(null);
		this.getManagedBean().setCreatedDtTo(null);
		this.getManagedBean().setStatus(null);
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
	 * Representive type change.
	 *
	 * @param member the member
	 */
	public void representiveTypeChange(ScCommitteeMember member) {
		Long paramId = member.getRepresentativeType().getParameterId();
		if (null != paramId) {
			member.setRepresentativeType(ParameterHelper.getInstance()
					.getPmParameterById(paramId));
		}
	}
	
	/**
	 * Goto qt search page.
	 * 
	 * @return the string
	 */
	public String viewSearchBackAction() {
		debugLogger("viewSearchBackAction");
		return NAV_TO_SEARCH_PAGE + "?faces-redirect=true";
	}
	
	public SelectItem[] getRecordStatusDropdownValues() {
		/*
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recordStatusList = new ArrayList<String>();
		recordStatusList.add(ParameterConst.RECORD_STATUS_ACTIVE);
		recordStatusList.add(ParameterConst.RECORD_STATUS_INACTIVE);
		*/
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recordStatusList = new ArrayList<String>();
		recordStatusList.add(ParameterConst.RECORD_STATUS_ACTIVE);
		recordStatusList.add(ParameterConst.RECORD_STATUS_INACTIVE);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode",SearchOperator.OPERATOR_IN.value(), recordStatusList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.RECORD_STATUS, searchBean);
		return items;
	}

}
