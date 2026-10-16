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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.criteria.JoinType;

import my.ep.biz.bean.sc.QtCommitteeMemberBean;
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
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.comparator.sc.MemberRoleComparator;
import my.ep.comparator.tpl.TplCommitteeTypeComparator;
import my.ep.comparator.tpl.TplMemberRoleComparator;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
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
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.Paginator;
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
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMemberHistory;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.tpl.comm.TplCommMember;
import my.ep.persistence.entity.tpl.comm.TplCommMemberHistory;
import my.ep.persistence.entity.tpl.comm.TplCommittee;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
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
import org.primefaces.event.TabChangeEvent;

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
	
	//35320 - START
	/** The Constant NAV_TO_FINALIZATION_COMMITTEE_MAINTENANCE. */
	private static final String NAV_TO_COMMITTEE_MAINTENANCE = "/xhtml/sc/committe/finalCommitteeMaintenance.xhtml";
	private static final String NAV_TO_COMMITTEE_ADD_SCREEN = "xhtml/sc/committe/committeeMaintenanceAddScreen.xhtml";
	// #37809 Start Vinod 
	private static final String NAV_TO_UPDATE_FC_CHAIRPERSON = "/xhtml/sc/committe/updateFinalizationChairperson.xhtml";
	// #37809 End Vinod 
	//35320 - END

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
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();

				populateUserSearchCriteria(userQCSearchBean, searchBean);
				// #37809 Start Vinod
				// Using CommitteeSearchBean for new drop down values and radio buttons.
				populateOrganizationSearchCriteria(getManagedBean().getCommitteeSearchBean(), searchBean);
				// #37809 End Vinod
				committeReqDTO.setUserSearchBean(searchBean);
				committeReqDTO.setPtjMinistryAdmin(Boolean.TRUE);
				
				//Enhancement #21689
				if(getManagedBean().getScQt()!=null && getManagedBean().getScQt().size()>0){
					committeReqDTO.setScQt(getManagedBean().getScQt().get(0));
				}
				
				return committeSO.retrieveUserListByCommType(committeReqDTO)
						.getUserResultBean();
			} finally {
				committeSO = null;
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
		String committeeType = getManagedBean().getQtCommittee().getScCommittee().getCommitteeType().getParameterCode();
		List<ScCommitteeMember> scCommMember = getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers();
		boolean hasChairperson = false;
		
		ICommitteSO committeeSO = null;
		
		try {
			committeeSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmUser(pmUser);
			reqDTO.setCommitteeType(committeeType);

			//Bug #19519 Secretary & Chairperson Procurement Board Sarawak & Sabah Name not displayed			
			if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) || 
					committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {

				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO mofReqDTO = new OrgProfileReqDTO();
				
				// 25638 - Start.
				//mofReqDTO.setOrgCode("103"); //KEMENTERIAN KEWANGAN MALAYSIA
				
				//OrgProfileResDTO mofResDTO = orgProfileSO.retrieveOrgProfileByCode(mofReqDTO);
				mofReqDTO.setOrgName("KEMENTERIAN KEWANGAN MALAYSIA");
				OrgProfileResDTO mofResDTO = orgProfileSO.retrieveOrgProfileByName(mofReqDTO);
				// 25638 - End.
				
				reqDTO.setPmOrgProfile(mofResDTO.getPmOrgProfile());	
				log.info(":::getRoleDesc() MOF ministry orgProfileId: "+ mofResDTO.getPmOrgProfile().getOrgProfileId()); //orgprofileid 955020
			
			} else {

				PmOrgProfile pmOrgProfile = getManagedBean().getQt().getPmOrgProfile();
				reqDTO.setPmOrgProfile(pmOrgProfile);
				
			}
			//end Bug #19519
			
			CommitteResDTO resDTO = committeeSO.checkIfUserHasRoleByRoleCodeCommTypeAndOrgProfile(reqDTO);
			
			if (resDTO.isSuccessful() && resDTO.isHasRequiredCommitteeRole()) {
				//Bug #24911
				for (ScCommitteeMember commMember : scCommMember) {
					if (commMember.getMemberRole().getParameterCode()
							.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
						hasChairperson = true;
						}
					}
				
				if(hasChairperson){
					PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
					pmUser.setMemberRoleId(memberRole.getParameterId());
					userRole = getParameterDesc(memberRole);
				}else{
					PmParameter chairPersonRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
					pmUser.setMemberRoleId(chairPersonRole.getParameterId());
					userRole = getParameterDesc(chairPersonRole);
				}
			}			
		} finally {
			committeeSO = null;
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
	 * Gets the search committee data model.
	 * 
	 * @return the search committee data model
	 */
	public IPagingDataModel<ScQtCommittee> getSearchQTDataModel() {
		IPagingDataModel<ScQtCommittee> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		if (searchQTDataModel == null) {
			searchQTDataModel = new QTPagingDataModel("searchQTs", getManagedBean()
					.getQtnTenderSearchBean());
			getManagedBean().setSearchQTDataModel(searchQTDataModel);
		}
		return searchQTDataModel;
	}
	
	/**
	 * Gets the clone search qt data model.
	 * 
	 * @return the clone search qt data model
	 */
	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();
		if (cloneSearchQTDataModel == null) {
			cloneSearchQTDataModel = new CloneQTPagingDataModel("clonesearchCommitteid",
					getManagedBean().getCloneQtnTenderSearchBean());
			getManagedBean().setCloneSearchQTDataModel(cloneSearchQTDataModel);
		}
		return cloneSearchQTDataModel;
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
				bean.setSearchField("scQt.createdDate"); //#35320
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
		
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE); //#35320
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A);
		committeeTypes.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD);
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
		committeReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		committeReqDTO.setRoleCode(getCommitteeRoleCode());
		committeReqDTO.setMaintanceFlow(StringUtils.isEmpty(this.getManagedBean().getTaskName()));//25037

		ScQt scQt = getManagedBean().getQt();
		String qtNo = scQt.getQtNo();

		// #35320 - Set QCA, QCB to QQC - Start
		String committeeTypeDesc = null;
		if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equals(scQtCommittee.getScCommittee()
				.getCommitteeType().getParameterCode())
				|| ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equals(scQtCommittee.getScCommittee()
						.getCommitteeType().getParameterCode())) {
			committeeTypeDesc = ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, 
					ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE);
		} else {

			committeeTypeDesc = ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
					scQtCommittee.getScCommittee().getCommitteeType().getParameterCode());
		}
		// #35320 - End
		
		CommitteResDTO resDTO = null;
		ICommitteSO committeSo = null;

		try {
			 committeSo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
		    resDTO = committeSo.validateFcCommitteDetails(committeReqDTO);
			resDTO = committeSo.updateCommitteDetails(committeReqDTO);

			if (resDTO.isSuccessful()) {
				//201123@asyrafhaziq_29316 - Start 
				if (CollectionUtils.isNotEmpty(committeReqDTO.getToBeDeletedMembers())){
					getManagedBean().setToBeDelCommitteMemebers(null);
				}
				//201123@asyrafhaziq_29316 - End
				if (StringUtils.equals(BpmOutcomeConst.BPM_SUBMIT, committeReqDTO.getOutcome())) {//25037
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
																qtNo + " - ", committeeTypeDesc},
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes().getUserLocale())));
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					renewCommitteeSessionObject();
					
					retrieveCommitteDetails(scQt.getQtId(), 
							scQtCommittee.getScCommittee().getCommitteeType().getParameterCode());

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
																qtNo + " - ", committeeTypeDesc},
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes().getUserLocale())));

				}
			}
		} finally {
			committeSo = null;
		}
		return null;
	}
	
	private void renewCommitteeSessionObject() {
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		String taskId = getManagedBean().getTaskId();
		String taskName = getManagedBean().getTaskName();
		String processName = getManagedBean().getProcessName();
		String[] outComes = getManagedBean().getOutComes();
		Object payLoad = getManagedBean().getPayLoad();
		
		super.renewSesionObject();
		
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setTaskName(taskName);
		getManagedBean().setProcessName(processName);
		getManagedBean().setOutComes(outComes);
		getManagedBean().setPayLoad(payLoad);
	}
	
	private String getCommitteeRoleCode(){
		return getCommitteMemberByUserId()
				.getRoleCode();
	}

	private CommitteResDTO getCommitteMemberByUserId() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ScQtCommittee scQtCommittee = getManagedBean().getQtCommittee();
		String committeeType = scQtCommittee.getScCommittee().getCommitteeType().getParameterCode();
		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO.setCommitteeType(committeeType);
		
		return committeSO.retrieveCommitteMemberByUserId(
				committeReqDTO);
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

		// If a record is deleted and added back to the same committee,
		// remove the record from deleted members list
		List<ScCommitteeMember> committeeMembers = getManagedBean().getCommitteMembers();
		List<ScCommitteeMember> deletedCommitteeMembers 
										= this.getManagedBean().getToBeDelCommitteMemebers();
		List<ScCommitteeMember> toBeAddedMembers = new ArrayList<ScCommitteeMember>();
		
		if (CollectionUtils.isNotEmpty(deletedCommitteeMembers) && CollectionUtils.isNotEmpty(committeeMembers)) {
			Map<String, ScCommitteeMember> currentCommMemberIcs = 
					new HashMap<String, ScCommitteeMember>();

			for (ScCommitteeMember currCommMem : committeeMembers) {
				currentCommMemberIcs.put(currCommMem.getIcPassport(),
						currCommMem);
			}

			for (ScCommitteeMember deletedCommitteeMember 
										: deletedCommitteeMembers) {
				if (currentCommMemberIcs.containsKey(
						deletedCommitteeMember.getIcPassport())) {
					ScCommitteeMember currentCommitteeMember = currentCommMemberIcs
											.get(deletedCommitteeMember.getIcPassport());
					
					deletedCommitteeMember=setCommitteeMemberData(deletedCommitteeMember, currentCommitteeMember);
					committeeMembers.remove(currentCommitteeMember);
					committeeMembers.add(deletedCommitteeMember);
					toBeAddedMembers.add(deletedCommitteeMember);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(
						toBeAddedMembers)) {
			deletedCommitteeMembers.removeAll(toBeAddedMembers);
		}
		if (CollectionUtils.isEmpty(
				deletedCommitteeMembers)) {
			getManagedBean().setToBeDelCommitteMemebers(null);
		}

		committeReqDTO.setScQtCommittee(scQtCommittee);
		committeReqDTO.setToBeDeletedMembers(
				getManagedBean().getToBeDelCommitteMemebers());
		FacesScopes facesScopes = new FacesScopes();
		if (null != this.getManagedBean().getPayLoad()) {
			SCEvaluationDataType evalDataType = (SCEvaluationDataType) this
					.getManagedBean().getPayLoad();
			committeReqDTO.setQtId(Long.valueOf(evalDataType.getQtId()));
			committeReqDTO.setCommitteeType(evalDataType.getCommitteeType());
			committeReqDTO.setPayLoad(evalDataType);
			committeReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			committeReqDTO.setTaskId(this.getManagedBean().getTaskId());

		} else {
			committeReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			committeReqDTO.setCommitteeType(this.getManagedBean()
					.getCommitteeType());
		}
		committeReqDTO.setUserLocale(facesScopes.getUserLocale());
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
		// #37809 Start Vinod 
		this.getManagedBean().setUpdateFCChairperson(false);
		// #37809 End Vinod 
		this.getManagedBean().setHeaderTitle(
				ParameterHelper.getInstance().getParameterDesc(
						ParameterTypeConst.SC_COMMITTEE_TYPE,
						scQtCommittee.getScCommittee().getCommitteeType().getParameterCode()));
		log.debug("Title : " + getManagedBean().getHeaderTitle());
		String committeeType = scQtCommittee.getScCommittee()
				.getCommitteeType().getParameterCode();
		log.debug("Committee Type : " + committeeType);
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
					committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
					committee
							.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				// #37809 Start Vinod
				sortScCommitteeMembers(qtCommitte.getScCommittee().getScCommitteeMembers());
				// #37809 End Vinod
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
				committeSO=null;
			}
			showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";

		} else if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE)) { //#35320 - add condition
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee()
					.getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			IParameterSO parameterSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
			            ModuleCodeConst.SUPPLIER_SELECTION, IParameterSO.class);
				committeSO.initializedFCLegacyCommitteeHistory(qtId);//25037
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
					committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
					committee
							.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				// Start #26189:PK2.1 
				if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) || 
						committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					for (ScCommitteeMember member : qtCommitte.getScCommittee().getScCommitteeMembers()) {
						if(!ParameterConst.COMMITTEE_ROLE_CHAIRPERSON
								.equals(member.getMemberRole().getParameterCode())){
						    if(member.getRepresentativeTypeId() == null && !ParameterConst.COMMITTEE_ROLE_SECRETARY
									.equals(member.getMemberRole().getParameterCode())){
								ParameterReqDTO roleReqDTO = new ParameterReqDTO();
								roleReqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
								roleReqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
								ParameterResDTO roleResDTO = parameterSO.retrieveParameterByTypeAndCode(roleReqDTO);
								member.setMemberRole(roleResDTO.getPmParameter());
								Long memberRoleId = SourcingConstants.MEMBER_ROLE_ID_CHAIRPERSON;
								member.setMemberRoleId(memberRoleId);
						    }
						}
					}
				}
				// End #26189:PK2.1
				for (ScCommitteeMember committeeMember : qtCommitte
						.getScCommittee().getScCommitteeMembers()) {
					if (committeeMember.getRepresentativeType() == null) {
						committeeMember.setRepresentativeType(new PmParameter());
						committeeMember.setRepresentativeTypeId(null);
					}
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
				this.getManagedBean().setScCommittee(qtCommitte.getScCommittee());
				this.getManagedBean().setCommitteeType(committeeType);
				
				this.getManagedBean().setCommitteeQTDataModel(
						new committeeQTPagingDataModel("qtDetailsId", getManagedBean().getScCommittee()));
			} finally {
				committeSO=null;
			}
			
			//#35320 - START
			this.getManagedBean().setViewCommitteePage(true);
			//#35320 - END
			// #37809 Start Vinod
			ScWorkFlowStatus scWorkFlowStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION,
					scQtCommittee.getScQt().getQtId());
			this.getManagedBean().setUpdateFCChairperson(scWorkFlowStatus != null
					&& QTStatusConst.SF_PENDING_SIGN_OFF.equals(scWorkFlowStatus.getPmStatus().getStatusId())
					&& isLoggedInUserSecretary(qtCommitte.getScCommittee()));
			// #37809 End Vinod
			
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
					committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
					committee
							.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
				this.getManagedBean().setScCommittee(qtCommitte.getScCommittee());
			} finally {
				committeSO=null;
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
		//#25037-START
		List<ScCommitteeMemberHistory> histories = this.getCommitteeSO().getMergedHistory(
				scQtCommittee, false);
		this.getManagedBean().setSelectedCommitteeHistory(null);
		this.getManagedBean().setMemberHistoryPaginator(new Paginator(new ArrayList<QtCommitteeMemberBean>()));
		this.getManagedBean().setCurrentCommitteeType(
				this.getParameterDesc(scQtCommittee.getScCommittee()
						.getCommitteeType()));
		this.getManagedBean().setHistoryPaginator(new Paginator(histories));
		//#25037-END
		return NAV_TO_VIEW_HISTORY;
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
				// Sort the committee members
				Collections.sort(resDTO.getCommittee().getScCommitteeMembers(),
						new MemberRoleComparator());
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
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				searchFilterBean.add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
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
		ICommitteSO committeSO =null;
		try {
			 committeSO = BusinessDelegate.getInstance()
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
			committeSO =null;
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
		detailSO=null;
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
	
	/**
	 * Event to handle selection of committee members.
	 * @param committeeMember
	 */
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
		String commType = getManagedBean().getQtCommittee().getScCommittee().getCommitteeType().getParameterCode();
		if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(commType)) {
			SelectItem[] pbbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
			this.getManagedBean().setAddRepresentativesTypeslist(
					Arrays.asList(pbbItems));
			//20200826@asyrafhaziq - 28600 -start
		}else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD.equalsIgnoreCase(commType)) {
			SelectItem[] ppbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PPR);
			this.getManagedBean().setAddRepresentativesTypeslist(
					Arrays.asList(ppbItems));
			//20200826@asyrafhaziq - 28600 -End
		} else if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(commType)) {
			SelectItem[] qcaItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
			this.getManagedBean().setAddRepresentativesTypeslist(
					Arrays.asList(qcaItems));
		}
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		// #37809 Start Vinod
		UserCommitteeSearchBean userCommitteeSearchBean = this.getManagedBean().getCommitteeSearchBean() != null ? this
				.getManagedBean().getCommitteeSearchBean() : new UserCommitteeSearchBean();
		if(StringUtils.isEmpty(userCommitteeSearchBean.getOrgCat())){
			userCommitteeSearchBean.setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		}
		this.getManagedBean().setCommitteeSearchBean(userCommitteeSearchBean);
		resetDeskOfficerSearchBean();
		this.getManagedBean().setFpbSearchProfile(isFpbSearchProfile());
		// #37809 End Vinod
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
		logger.debug("User Selection : " + userSelection);
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
			getManagedBean().getCommitteMember().setRepresentativeType(
					new PmParameter());
			getManagedBean().getCommitteMember().setRepresentativeTypeId(null);
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
		/*List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getSelectedCommitteMemebers();
		if (CollectionUtils.isEmpty(selectedList)) {
			throw new EpException(MessageConst.ERROR_760146);
		}*/
		List<ScCommitteeMember> deleteList=new ArrayList<ScCommitteeMember>();
		if (null==toBeDelCommitteMemebers) {
			toBeDelCommitteMemebers = new ArrayList<ScCommitteeMember>();
			this.getManagedBean().setToBeDelCommitteMemebers(toBeDelCommitteMemebers);	
		}
		
		for (int i = 0; i < committeeMemberList.size(); i++) {
			if(committeeMemberList.get(i).isSelected()){
			//committeeMemberList.remove(committeeMemberList.get(i));
				committeeMemberList.get(i).setDeleted(true);
				deleteList.add(committeeMemberList.get(i));
			}
		}
		if(CollectionUtils.isNotEmpty(deleteList)){
			committeeMemberList.removeAll(deleteList);
		}
		//201123@asyrafhaziq_29316 - Start
		this.getManagedBean().setCommitteMembers(committeeMemberList);
		//201123@asyrafhaziq_29316 - End
		this.getManagedBean().setSelectedCommitteMemebers(null);
		
		toBeDelCommitteMemebers.addAll(deleteList);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserQCSearchBean().setName(null);
		this.getManagedBean().getUserQCSearchBean().setIdentificationType(null);
		this.getManagedBean().getUserQCSearchBean().setIdentificationNo(null);
		// #37809 Start Vinod
		if(this.getManagedBean().getCommitteeSearchBean() != null){
			resetDeskOfficerSearchBean();
		}
		// #37809 End Vinod
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
		ScCommitteeMember scMember = getManagedBean().getCommitteMember();
		if (scMember.getUserSelection().equals("N")) {
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(scMember.getMemberName());
			member.setIcPassport(scMember.getIcPassport());
			member.setGrade(scMember.getGrade());
			member.setDesignation(scMember.getDesignation());
			member.setPNP(scMember.isPNP());
			member.setEmail(scMember.getEmail());
			member.setMemberRole(scMember.getMemberRole());
			if (member.getMemberRole() != null) {
				member.setMemberRoleId(member.getMemberRole().getParameterId());	
			}			
			member.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
			setCommitteeMemberOrgProfile(member);

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
					setCommitteeMemberOrgProfile(committeeMember);
					if (users.get(i).isPMP() == true) {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
						reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
						ParameterResDTO resDTO = getParameterSO()
								.retrieveParameterByTypeAndCode(reqDTO);
						committeeMember.setMemberRole(resDTO.getPmParameter());
					} else {
						if (null != users.get(i).getMemberRoleId()) {
							ParameterReqDTO reqDTO = new ParameterReqDTO();
							reqDTO.setParameterId(users.get(i).getMemberRoleId());
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterById(reqDTO);
							committeeMember.setMemberRole(resDTO
									.getPmParameter());
						} else {
							committeeMember.setMemberRole(new PmParameter());
						}
					}
					if (committeeMember.getMemberRole() != null) {
						committeeMember.setMemberRoleId(committeeMember.getMemberRole().getParameterId());
					}
					committeeMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
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
				Integer value = ParameterConst.COMMITTEE_ROLE_OTHER_VALUE; //20230706@asyrafhaziq- SQUBE Bug NullPointer#3

				if (memberRole == null) {
					value = ParameterConst.COMMITTEE_ROLE_OTHER_VALUE;
				} else {
					if (memberRole
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
						value = ParameterConst.COMMITTEE_ROLE_CHAIRPERSON_VALUE;
					} else if (memberRole
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
						value = ParameterConst.COMMITTEE_ROLE_SECRETARY_VALUE;
					} else if (memberRole
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
						value = ParameterConst.COMMITTEE_ROLE_MEMBER_VALUE;
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
		ScCommitteeMember scMemeber = getManagedBean().getCommitteMember();
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(scMemeber.getMemberName());
			member.setIcPassport(scMemeber.getIcPassport());
			member.setGrade(scMemeber.getGrade());
			member.setDesignation(scMemeber.getDesignation());
			member.setPNP(scMemeber.isPNP());
			member.setEmail(scMemeber.getEmail());
			member.setMemberRole(scMemeber.getMemberRole());
			if (member.getMemberRole() != null) {
				member.setMemberRoleId(member.getMemberRole().getParameterId());	
			}
			member.setRepresentativeType(scMemeber
					.getRepresentativeType());
			if (member.getRepresentativeType() != null) {
				member.setRepresentativeTypeId(member.getRepresentativeType().getParameterId());
			}
			member.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
			setCommitteeMemberOrgProfile(member);

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
			if (CollectionUtils.isNotEmpty(
					this.getManagedBean().getSelectedPmUser())) {
				for (PmUser user : getManagedBean().getSelectedPmUser()) {
					ScCommitteeMember committeeMember = new ScCommitteeMember();
					committeeMember.setMemberName(user.getUserName());
					committeeMember.setIcPassport(user.getIdentificationNo());
					committeeMember.setDesignation(user.getDesignation());
					committeeMember.setEmail(user.getEmail());
					committeeMember.setGrade(user.getGrade());
					committeeMember.setPNP(user.isPMP());
					committeeMember.setPmUser(user);
					setCommitteeMemberOrgProfile(committeeMember);
					
					if (user.getMemberRoleId() == null) {
						PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
								ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
						user.setMemberRoleId(memberRole.getParameterId());
					} 
					PmParameter userMemberRole = ParameterHelper.getInstance().getPmParameterById(
							user.getMemberRoleId());
					committeeMember.setMemberRole(userMemberRole);		
					if (committeeMember.getMemberRole() != null) {
						committeeMember.setMemberRoleId(committeeMember.getMemberRole().getParameterId());	
					}
	
					committeeMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
					if (ParameterConst.COMMITTEE_ROLE_MEMBER
							.equals(userMemberRole.getParameterCode())) {
						committeeMember.setRepresentativeType(new PmParameter());
					} else {
						committeeMember.setRepresentativeType(null);						
					}
					if (committeeMember.getRepresentativeType() != null) {
						committeeMember.setRepresentativeTypeId(committeeMember.getRepresentativeType().getParameterId());
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
		
		Collections.sort(memberList, new MemberRoleComparator());

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
					member1.setBrandNew(true);
					member1.setCommitteeMemberId(null);
					setUserDetails(member1);
					setCommitteeMemberOrgProfile(member1);
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
		scCommittee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_COMPLETED));
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
		iUserSO=null;
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
		orgProfileSO=null;

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
	
	/**
	 * Override from SourcingBaseBackingBean#preRenderView() and retrive committee details.
	 */
	public void preRenderView() {
		Object payLoad = getBpmTaskDetails(Object.class, null);
		//#31870(16122021)
		FacesScopes facesScopes = new FacesScopes();
		Long loggedInUserId = facesScopes.getUserSessionBean().getUserId();
		if (SCProcessNameConst.ASSIGN_FC_MEMBER.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) payLoad;
			//#31870 - START
			/*retrieveCommitteDetails(Long.valueOf(evaluationDataType.getQtId()), evaluationDataType
					.getCommitteeType());*/
			retrieveCommitteDetailsWithChecking(Long.valueOf(evaluationDataType.getQtId()), evaluationDataType
					.getCommitteeType(),loggedInUserId);//#31870(16122021)
			//#31870 - END
			getManagedBean().setPayLoad(evaluationDataType);
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true");
	}
	
	private void retrieveCommitteDetails(Long qtId, String committeeType) {
		ICommitteSO committeSO = null;
		try {
			committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			
			//#35320 - START
			if (null != qtId) {
				committeReqDTO.setQtId(qtId);
			}
			//#35320 - END

			committeReqDTO.setCommitteeType(committeeType);
			// committeReqDTO.setCommitteeType("QCA");
			CommitteResDTO committeResDTO = committeSO
					.retrieveCommitteByQtIdNCommType(committeReqDTO);
			ScQtCommittee qtCommittee = committeResDTO.getScQtCommittee();
			// #37809 Start Vinod
			sortScCommitteeMembers(qtCommittee.getScCommittee().getScCommitteeMembers());
			// #37809 End Vinod
			this.getManagedBean().setQtCommittee(qtCommittee);
			this.getManagedBean().setHeaderTitle(
					ParameterHelper.getInstance().getParameterDesc(
							ParameterTypeConst.SC_COMMITTEE_TYPE,
							qtCommittee.getScCommittee().getCommitteeType().getParameterCode()));
			
			this.getManagedBean().setScCommittee(qtCommittee.getScCommittee());
			this.getManagedBean().setCommitteeType(committeeType);
			this.getManagedBean().setQt(qtCommittee.getScQt());			
			this.getManagedBean().setCommitteeQTDataModel(
					new committeeQTPagingDataModel("qtDetailsId", getManagedBean().getScCommittee()));
		} finally {
			committeSO = null;
		}
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
	
	private ICommitteSO getCommitteeSO(){
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
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
	 * Goto qt search page.
	 * 
	 * @return the string
	 */
	public String viewSearchBackAction() {
		debugLogger("viewSearchBackAction");
		return NAV_TO_SEARCH_PAGE + "?faces-redirect=true";
	}
	
	/**
	 * Gets the Status DropDown Values.
	 *
	 * @return SelectItem[]
	 */
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

	/**
	 * Check whether Date is null or not
	 * 
	 * @param publishDate
	 * @param createdDate
	 * @return Date
	 */
	public Date retriveDatesforOrgProfile(Date publishDate,Date createdDate){
		if(publishDate==null){
			log.info("===========createdDate====="+createdDate);
			return createdDate;
		}
		log.info("===========publishDate====="+publishDate);
		return publishDate;
		
	}
	
	// 25037 - start
	/**
	 * Populate selected history and display the currently selected member
	 * history.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void onSelectHistory() throws IOException {
		ScCommitteeMemberHistory history = this.getManagedBean().getSelectedCommitteeHistory();
		List<QtCommitteeMemberBean> list = (List<QtCommitteeMemberBean>) (Object) history.deserializeList(history.getCommitteeMembers());
		CustomUtils.sortQtCommitteeMemberRole(list);
		this.getManagedBean().setMemberHistoryPaginator(new Paginator(list));
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setScCommitteeId(history.getScCommittee().getCommitteeId());
		CommitteResDTO resDTO = this.getCommitteeSO().retrieveCommitteDetailsByCommitteeId(reqDTO);
		history.setScCommittee(resDTO.getCommittee());
	}

	// 25037 - end
	
	//#31870 - START
	//Edit parameter datatype -#31870 (16122021)
	private void retrieveCommitteDetailsWithChecking(Long qtId, String committeeType, Long userId) {
		ICommitteSO committeSO = null;
		try {
			log.info("USER_ID === "+userId);
			committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setQtId(qtId);
			committeReqDTO.setCommitteeType(committeeType);
			// committeReqDTO.setCommitteeType("QCA");
			CommitteResDTO committeResDTO = committeSO
					.retrieveCommitteByQtIdNCommType(committeReqDTO);
			ScQtCommittee qtCommittee = committeResDTO.getScQtCommittee();
			// #37809 Start Vinod
			sortScCommitteeMembers(qtCommittee.getScCommittee().getScCommitteeMembers());
			// #37809 End Vinod
			this.getManagedBean().setQtCommittee(qtCommittee);
			this.getManagedBean().setHeaderTitle(
					ParameterHelper.getInstance().getParameterDesc(
							ParameterTypeConst.SC_COMMITTEE_TYPE,
							qtCommittee.getScCommittee().getCommitteeType().getParameterCode()));
			List<ScCommitteeMember> committeeMemberList = qtCommittee.getScCommittee().getScCommitteeMembers();
			Boolean checkIsExistFCSecretary = Boolean.FALSE;
			
			if(isNotNullOREmpty(committeeMemberList)){
				for(int a=0; a < committeeMemberList.size(); a++){
					log.info("Member Role == "+committeeMemberList.get(a).getMemberRole().getParameterCode());
					if("S".equalsIgnoreCase(committeeMemberList.get(a).getMemberRole().getParameterCode())){
						checkIsExistFCSecretary = Boolean.TRUE;
						break;
					}
				}
			}
			//Edit condition #31870 (16122021)
			if(!checkIsExistFCSecretary){
				log.info("No FC Secretary is exist inside the FC Committee == ");
				try{
					//Insert record for FC Secretary, due to issued on
					//FC Secretary record is missing inside table SC_COMMITTEE_MEMBER for selected QT
					ScCommitteeMember secretaryMember = new ScCommitteeMember();
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(userId);//Edit #31870 (16122021)
					PmUser user = getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();
					secretaryMember.setPmUser(user);
					secretaryMember.setMemberName(user.getUserName());
					secretaryMember.setIcPassport(user.getIdentificationNo());
					secretaryMember.setGrade(user.getGrade());
					if (null != user.getDesignation()) {
						secretaryMember.setDesignation(user.getDesignation());
					}
					secretaryMember.setPNP(user.isPMP());
					secretaryMember.setEmail(user.getEmail());
					ParameterReqDTO roleReqDTO = new ParameterReqDTO();
					roleReqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_SECRETARY);
					roleReqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
					ParameterResDTO roleResDTO = getParameterSO().retrieveParameterByTypeAndCode(roleReqDTO);
					secretaryMember.setMemberRole(roleResDTO.getPmParameter());
					if (secretaryMember.getMemberRole() != null) {
						secretaryMember.setMemberRoleId(secretaryMember.getMemberRole().getParameterId());	
					}
					secretaryMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
					PmOrgProfile pmOrgProfile = null;
					OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
					reqDTO.setUserId(userId);//Edit #31870 (16122021)
					reqDTO.setOrgType(OrganizationType.DIVISION.value());
					OrgProfileResDTO resDTO = getOrgProfileSO()
							.retrieveOrgProfileListByOrgType(reqDTO);
					List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
					if (pmOrgProfiles != null && pmOrgProfiles.size() > 0) {
						pmOrgProfile = pmOrgProfiles.get(0);
					}
					
					secretaryMember.setPmOrgProfile(pmOrgProfile);
					secretaryMember.setScCommittee(qtCommittee.getScCommittee());
					getCommitteeSO().saveCommitteeMember(secretaryMember);
					
					renewCommitteeSessionObject();
					retrieveCommitteDetails(qtId,committeeType);
				}catch(Exception e){
					log.info("Error During Insert === ");
				}
			}
			this.getManagedBean().setScCommittee(qtCommittee.getScCommittee());
			this.getManagedBean().setCommitteeType(committeeType);
			this.getManagedBean().setQt(qtCommittee.getScQt());			
			this.getManagedBean().setCommitteeQTDataModel(
					new committeeQTPagingDataModel("qtDetailsId", getManagedBean().getScCommittee()));
		} finally {
			committeSO = null;
		}
	}
	//#31870 - END

	// 25037 - start
	/**
	 * Evaluate active flag for scCommittee
	 * 
	 * @param committee
	 * @return boolean isCommitteeRecordStatusActive
	 */
	public boolean isCommitteeRecordStatusActive(ScCommittee committee) {
		if (null == committee) {
			return false;
		}
		String committeeType = committee.getCommitteeType().getParameterCode();
		List<ScQtCommittee> qtCommittees = committee.getScQtCommittees();
		if (Arrays.asList(SourcingConstants.COMMITTEES_ORDER).contains(committeeType) && null != qtCommittees
				&& qtCommittees.size() <= 1) {
			return true;
		}
		return committee.isRecordStatus();
	}
	// 25037 - end
	
	//35320 - START
	/**
	 * Evaluate active flag for scCommittee
	 * 
	 * @param committee
	 * @return boolean isCommitteeRecordStatusActive
	 */
	public boolean isLatestCommitteeRecordStatusActive(TplCommittee committee) {

		if (null == committee) {
			return false;
		}

		String committeeType = committee.getCommitteeType().getParameterCode();
		if (Arrays.asList(SourcingConstants.getFinalCommitteesOrder()).contains(committeeType)) {
			return true;
		}
		return committee.isRecordStatus();
	}
	// #35320 - END

	//35320 - START
	/**
	 * Method to retrieve Committee Type detail based on the tab id
	 */
	public void getTabInfo() {
		Long loggedInUserId = getUserIdSessionFromLoginId();
		
		ICommitteSO committeeSO = null;
		TplCommittee displayCommittee = null;

		try {
			committeeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					ICommitteSO.class);
			List<TplCommittee> committeeList = committeeSO.getCommitteesByUserId(loggedInUserId);
			CommitteReqDTO reqDto = new CommitteReqDTO();
			if (CollectionUtils.isNotEmpty(committeeList)) {
				Collections.sort(committeeList, new TplCommitteeTypeComparator());

				List<String> commList = getCommitteeList(committeeList);
				Map<String, TplCommittee> committeeMap = getCommitteeMap(committeeList);

				TplCommittee newCommittee = getDisplayCommittee(committeeMap, reqDto);				
				displayCommittee = newCommittee;

				this.getManagedBean().setCommitteMap(committeeMap);
				int activeIndex;
				if (StringUtils.isNotBlank(this.getManagedBean().getSelectedTab())) {
					activeIndex = commList.indexOf(this.getManagedBean().getSelectedTab());
					this.getManagedBean().setActiveIndex(activeIndex);
				}
			}

			reqDto.setTplCommitteeId(null != displayCommittee ? displayCommittee.getCommitteeId() : null);
			reqDto.setOmitCurrent(false);
			reqDto.setUserId(loggedInUserId);

			CommitteResDTO resDto = this.getCommitteeSO().getCommitteeMergedHistory(reqDto);
			List<TplCommMemberHistory> histories = new ArrayList<>();
			if (null != resDto.getTplMemberHistory()) {
				histories = resDto.getTplMemberHistory();
			}

			List<TplCommMember> latestCommittee = resDto.getTplMemberList();
			if (null != latestCommittee) {
				Collections.sort(latestCommittee, new TplMemberRoleComparator());
				setTplCommMemberRepresentativeType(latestCommittee);
			}

			TplCommittee scCommittee = resDto.getTplCommittee();
			TplCommMember loggedInMember = getSecretaryMember(latestCommittee);
			this.getManagedBean().setTplCommMember(loggedInMember);
			if (null != getManagedBean().getTplCommMember()) {
				retrieveTplUserDetails();
			}

			Paginator historyPaginator = new Paginator(histories);
			List<?> historyModel = historyPaginator.getModel();
			TplCommMemberHistory selectedHistory = getCommitteeMemberHistory(historyModel);

			this.getManagedBean().setTplCommittee(scCommittee);
			this.getManagedBean().setCommitteeType(
					null != displayCommittee ? displayCommittee.getCommitteeType().getParameterCode() : null);
			this.getManagedBean().setSelectedTplCommHistory(selectedHistory);
			this.getManagedBean().setMemberHistoryPaginator(new Paginator(new ArrayList<QtCommitteeMemberBean>()));
			this.getManagedBean().setCurrentCommitteeType(
					null != displayCommittee ? displayCommittee.getCommitteeType().getParameterCode() : null);
			this.getManagedBean().setHistoryPaginator(new Paginator(histories));
			this.getManagedBean().setLatestMembers(latestCommittee);

			setHeaderTitle(scCommittee);
			onSelectCommitteeHistory();
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION, null, null,
					"CommitteBackingBean.getTabInfo", null, ex);
		}
		getTheEndTabProcess();
		// #37809 Start Vinod
		UserCommitteeSearchBean userCommitteeSearchBean = this.getManagedBean().getCommitteeSearchBean() != null ? this
				.getManagedBean().getCommitteeSearchBean() : new UserCommitteeSearchBean();
		this.getManagedBean().setCommitteeSearchBean(userCommitteeSearchBean);
		// #37809 End Vinod
	}
	// #35320 - END

	//35320 - START
	/**
	 * Retrieves the committee based on the tab clicked by the user.
	 * 
	 * @param Map
	 *            consist of committee type and the committee
	 * 
	 * @return the committee associated with the specified tab
	 */
	private TplCommittee getDisplayCommittee(Map<String, TplCommittee> committeeMap, CommitteReqDTO reqDto) {
		TplCommittee displayCommittee;
		if (StringUtils.isBlank(this.getManagedBean().getSelectedTab()) && !committeeMap.isEmpty()) {
			Iterator<TplCommittee> valueIterator = committeeMap.values().iterator();
			displayCommittee = valueIterator.next();
		} else {
			displayCommittee = committeeMap.get(this.getManagedBean().getSelectedTab());
		}
		reqDto.setPrevTplCommittee(displayCommittee);
		return displayCommittee;
	}
	// #35320 - END

	//35320 - START
	/**
	 * Retrieves the secretary of the committee.
	 * 
	 * @param list
	 *            of committee member
	 * 
	 * @return the secretary committee member, or null if not present
	 */
	private TplCommMember getSecretaryMember(List<TplCommMember> latestCommittee) {
		TplCommMember loggedInMember = null;
		if (CollectionUtils.isNotEmpty(latestCommittee)) {
			for (TplCommMember committeeMem : latestCommittee) {
				if (committeeMem.getMemberRole().getParameterCode().equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
					loggedInMember = committeeMem;
					return loggedInMember;
				}
			}
		}
		return loggedInMember;
	}
	// #35320 - END

	//35320 - START
	/**
	 * Retrieves the committee history
	 * 
	 * @param list
	 *            of committee history model
	 * 
	 * @return the committee history, or null if not present
	 */
	private TplCommMemberHistory getCommitteeMemberHistory(List<?> historyModel) {
		TplCommMemberHistory selectedHistory = null;
		if (CollectionUtils.isNotEmpty(historyModel)) {
			for (Object item : historyModel) {
				if (item instanceof TplCommMemberHistory) {
					selectedHistory = (TplCommMemberHistory) item;
					break;
				}
			}
		}
		return selectedHistory;
	}
	// #35320 - END

	//35320 - START
	/**
	 * Handles the tab change event and retrieves the committee type detail
	 * based on the selected tab.
	 * 
	 * @param event
	 *            the tab change event
	 */
	public void onTabChangeAction(TabChangeEvent event) {
		String tabId = event.getTab().getId();
		this.getManagedBean().setSelectedTab(tabId.toUpperCase());
		getTabInfo();
	}
	// #35320 - END

	//35320 - START
	/**
	 * Gets the role list excluding the secretary role.
	 * 
	 * @return an array of SelectItem representing the available roles
	 */
	public SelectItem[] getRoleList() {

		SelectItem[] items = getDropdownValues(ParameterTypeConst.COMMITTEE_ROLE);

		Long valueToRemove = SourcingConstants.MEMBER_ROLE_ID_SECRETARY;
		List<SelectItem> tempList = new ArrayList<>();
		for (SelectItem item : items) {
			Long itemValue = (Long) item.getValue();
			if (!valueToRemove.equals(itemValue)) {
				tempList.add(item);
			}
		}
		return tempList.toArray(new SelectItem[0]);
	}
	// #35320 - END

	//35320 - START
	/**
	 * Handles the role change event for a committee member.
	 * 
	 * @param committeemember
	 *            the committee member whose role is being changed
	 */
	public void onRoleChange(TplCommMember committeemember) {

		Long selectedRoleId = committeemember.getMemberRole().getParameterId();
		PmParameter selectedRole = ParameterHelper.getInstance().getPmParameterById(selectedRoleId);

		if (selectedRole != null) {
			committeemember.setMemberRole(selectedRole);
		}

		if (null != selectedRole && (!SourcingConstants.MEMBER.equalsIgnoreCase(selectedRole.getParameterCode()))) {
			committeemember.setRepresentativeTypeId(null);
		}
	}
	// #35320 - END

	//35320 - START
	/**
	 * Finalizes the committee setup by adding selected users as committee
	 * members.
	 * 
	 * @throws EpException
	 *             if no user is selected or if duplicate members are found.
	 */
	public void okFinalizeCommitteeAction() {

		getManagedBean().setAutoUpdate(Boolean.TRUE);

		TplCommittee tplCommittee = this.getManagedBean().getTplCommittee();

		List<TplCommMember> memberList = tplCommittee.getTplCommMembers();
		if (null == memberList) {
			memberList = new ArrayList<>();
		}

		List<TplCommMember> tobeAddedList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getSelectedPmUser())) {

			for (PmUser user : getManagedBean().getSelectedPmUser()) {
				TplCommMember committeeMember = new TplCommMember();
				committeeMember.setMemberName(user.getUserName());
				committeeMember.setIcPassport(user.getIdentificationNo());
				committeeMember.setDesignation(user.getDesignation());
				committeeMember.setEmail(user.getEmail());
				committeeMember.setGrade(user.getGrade());
				committeeMember.setPNP(user.isPMP());
				committeeMember.setPmUser(user);
				setTplCommMemberOrgProfile(committeeMember);

				if (user.getMemberRoleId() == null) {
					PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
					user.setMemberRoleId(memberRole.getParameterId());
				}

				PmParameter userMemberRole = ParameterHelper.getInstance().getPmParameterById(user.getMemberRoleId());
				committeeMember.setMemberRole(userMemberRole);
				if (committeeMember.getMemberRole() != null) {
					committeeMember.setMemberRoleId(committeeMember.getMemberRole().getParameterId());
				}

				committeeMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
				setMemberValue(userMemberRole, committeeMember);

				if (committeeMember.getRepresentativeType() != null) {
					committeeMember.setRepresentativeTypeId(committeeMember.getRepresentativeType().getParameterId());
				}

				committeeMember.setTplCommittee(tplCommittee);
				tobeAddedList.add(committeeMember);
			}
		} else {
			throw new EpException(MessageConst.ERROR_760121);
		}

		if (isTplMemberExists(tplCommittee.getTplCommMembers(), tobeAddedList)) {
			throw new EpException(MessageConst.ERROR_760048, new String[] { ParameterHelper.getInstance()
					.getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
							tplCommittee.getCommitteeType().getParameterCode()) });

		}
		memberList.addAll(tobeAddedList);

		Collections.sort(memberList, new TplMemberRoleComparator());

		this.getManagedBean().setTplCommMembers(memberList);
		tplCommittee.setTplCommMembers(memberList);

		RequestContext.getCurrentInstance().execute("committeeScreen3.hide()");
	}
	// #35320 - END

	//35320 - START
	private void setMemberValue(PmParameter userMemberRole, TplCommMember committeeMember) {
		if (ParameterConst.COMMITTEE_ROLE_MEMBER.equals(userMemberRole.getParameterCode())) {
			committeeMember.setRepresentativeType(new PmParameter());
		} else {
			committeeMember.setRepresentativeType(null);
		}
	}
	// #35320 - END

	//35320 - START
	/**
	 * Validate username field is not blank
	 */
	public void validateUser() {

		String user = this.getManagedBean().getUserQCSearchBean().getName();

		FacesContext fc = FacesContext.getCurrentInstance();

		if (StringUtils.isBlank(user)) {
			addFacesMessage(fc, MessageConst.ERROR_760802, null);
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_TO_COMMITTEE_ADD_SCREEN);

		} else {
			searchUserAction();
		}
	}
	// #35320 - END
	
	//35320 - START
	private List<QtCommitteeMemberBean> sortMemberByAlphabetOrder(List<QtCommitteeMemberBean> committeeMember){

		List<QtCommitteeMemberBean> newCommittee = new ArrayList<>();
		List<QtCommitteeMemberBean> memberList = new ArrayList<>();
		
		if(CollectionUtils.isNotEmpty(committeeMember)){
			for(QtCommitteeMemberBean bean : committeeMember){
				if(bean.getMemberRole().equals(ParameterConst.COMMITTEE_ROLE_MEMBER)){
					memberList.add(bean);
				} else {
					newCommittee.add(bean);
				}
			}
		}

		// Sort using Comparator
        Collections.sort(memberList, new Comparator<QtCommitteeMemberBean>() {
            @Override
            public int compare(QtCommitteeMemberBean u1, QtCommitteeMemberBean u2) {
                return u1.getMemberName().compareToIgnoreCase(u2.getMemberName());
            }
        });
        
        if(CollectionUtils.isNotEmpty(memberList)){
        	for(QtCommitteeMemberBean bean2 : memberList){
            	newCommittee.add(bean2);
            }
        }
		return newCommittee;
	}
	// #35320 - END

	// #35320 - START
	private Map<String, TplCommittee> getCommitteeMap(List<TplCommittee> committeeList) {
		Map<String, TplCommittee> committeeMap = new LinkedHashMap<>();
		for (TplCommittee committee : committeeList) {
			String paramCode = committee.getCommitteeType().getParameterCode();
			committeeMap.put(paramCode, committee);
		}
		return committeeMap;
	}
	// #35320 - END

	// #35320 - START
	private List<String> getCommitteeList(List<TplCommittee> committeeList) {
		List<String> commList = new ArrayList<>();
		for (TplCommittee committee : committeeList) {
			String paramCode = committee.getCommitteeType().getParameterCode();
			commList.add(paramCode);
		}
		return commList;
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Populate selected history and display the currently selected member
	 * history.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void onSelectCommitteeHistory() throws IOException {
		TplCommMemberHistory history = this.getManagedBean().getSelectedTplCommHistory();
		List<QtCommitteeMemberBean> list;
		CommitteReqDTO reqDTO = new CommitteReqDTO();

		if (null != history) {
			list = (List<QtCommitteeMemberBean>) (Object) history.deserializeList(history.getCommitteeMembers());
			CustomUtils.sortQtCommitteeMemberRole(list);
			list = sortMemberByAlphabetOrder(list);
			this.getManagedBean().setMemberHistoryPaginator(new Paginator(list));
			reqDTO.setTplCommitteeId(history.getTplCommittee().getCommitteeId());
		} else {
			if (null != this.getManagedBean().getTplCommittee()) {
				reqDTO.setTplCommitteeId(this.getManagedBean().getTplCommittee().getCommitteeId());
			}
		}

		if (null != reqDTO.getTplCommitteeId()) {
			CommitteResDTO resDTO = this.getCommitteeSO().retrieveCommitteDtlByCommitteeId(reqDTO);
			if (null != history) {
				history.setTplCommittee(resDTO.getTplCommittee());
			}
		}
	}
	// #35320 - END

	// #35320 - START
	private void setHeaderTitle(TplCommittee tplCommittee) {
		if (null != tplCommittee) {
			this.getManagedBean().setHeaderTitle(
					ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
							tplCommittee.getCommitteeType().getParameterCode()));
		} else {
			this.getManagedBean().setHeaderTitle("");
		}
	}
	// #35320 - END
	
	// #35320 - START
	private void getTheEndTabProcess(){
		FacesContext fc = FacesContext.getCurrentInstance();
		if (!getManagedBean().isRefreshFCMaintenancePage()) {
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_TO_COMMITTEE_MAINTENANCE + "?faces-redirect=true");
		} else {
			getManagedBean().setRefreshFCMaintenancePage(Boolean.FALSE);
		}
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Retrieve user details.
	 *
	 * @param userid the userid
	 */
	public void retrieveTplUserDetails() {

		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(getManagedBean().getTplCommMember().getPmUser().getUserId());
		UserResDTO userResDTO = iUserSO.retrieveUserByUserId(reqDTO);
		PmUser pmUser = userResDTO.getPmUser();

		TplCommMember committeeMember = getManagedBean().getTplCommMember();

		committeeMember.setMemberName(pmUser.getUserName());
		committeeMember.setIcPassport(pmUser.getIdentificationNo());
		committeeMember.setDesignation(pmUser.getDesignation());
		committeeMember.setGrade(pmUser.getGrade());
		UserResDTO userResDTO1 = iUserSO.retrieveUserOrgListByUserId(reqDTO);
		List<PmUserOrg> userOrgs = userResDTO1.getUserOrgList();
		List<PmOrgProfile> minstryProfiles = new ArrayList<>();
		for (PmUserOrg pmUserOrg : userOrgs) {
			PmOrgProfile pmOrgProfile = pmUserOrg.getPmOrgProfile();

			if (pmOrgProfile != null && pmOrgProfile.getParentOrgProfile() != null) {
				minstryProfiles.add(pmOrgProfile.getParentOrgProfile().getParentOrgProfile());
			}
		}
		iUserSO = null;
		getManagedBean().setMinstryList(minstryProfiles);
		getManagedBean().setTplCommMember(committeeMember);
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Adds the committe member.
	 *
	 * @param titleName the title name
	 */
	public void addTplCommitteeMember(String titleName) {

		IPagingDataModel<PmUser> userSearhQCDataModel = getManagedBean().getUserSearchQCDataModel();
		if (userSearhQCDataModel == null) {
			this.getManagedBean().setUserSearchQCDataModel(
					new UserQCPagingDataModel("userexistid", getManagedBean().getUserQCSearchBean()));
		}

		UserCommitteeSearchBean userQCSearchBean = getManagedBean().getUserQCSearchBean();
		userQCSearchBean.setName(null);
		userQCSearchBean.setIdentificationNo(null);
		userQCSearchBean.setIdentificationType(null);

		getManagedBean().getUserSearchQCDataModel().resetData();
		this.getManagedBean().setTplCommMember(null);
		this.getManagedBean().setSelectedPmUser(null);

		TplCommMember committeeMember = new TplCommMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(QTConstants.USER_SELECTION);
		this.getManagedBean().setTplCommMember(committeeMember);

		getSelectionTypes();
		getManagedBean().setHeaderTitle(titleName);
		String commType = getManagedBean().getTplCommittee().getCommitteeType().getParameterCode();

		if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(commType)) {
			SelectItem[] pbbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
			this.getManagedBean().setAddRepresentativesTypeslist(Arrays.asList(pbbItems));
		} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD.equalsIgnoreCase(commType)) {
			SelectItem[] ppbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PPR);
			this.getManagedBean().setAddRepresentativesTypeslist(Arrays.asList(ppbItems));
		} else if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(commType)) {
			SelectItem[] qcaItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
			this.getManagedBean().setAddRepresentativesTypeslist(Arrays.asList(qcaItems));
		}
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		// #37809 Start Vinod
		if (this.getManagedBean().getQt() == null) {
			ScQt scQt = constructQTtoSearchDropdownValues();
			resetCommitteeSearchBean(scQt);
			// this qt is used to get PTJ dropdown values only in maintanence.
			this.getManagedBean().setQt(scQt);
		} else {
			resetDeskOfficerSearchBean();
		}
		this.getManagedBean().setFpbSearchProfile(isFpbSearchProfile());
		// #37809 End Vinod
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Returns the committee role description for the given user.
	 * <p>
	 * Checks the user's eligibility for the committee role based on the
	 * committee type, organization profile, and existing members.
	 * Assigns Chairperson role if none exists, otherwise assigns Member role.
	 * </p>
	 *
	 * @param pmUser the user whose role description is to be determined
	 * @return the role description assigned to the user; defaults to Member if undetermined
	 */
	public String getTplRoleDesc(PmUser pmUser) {
		// #37809 Start Vinod 
		if (isFcChairpersonUpdateView()) {
			PmParameter chairPersonRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
					ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
			pmUser.setMemberRoleId(chairPersonRole.getParameterId());
			return getParameterDesc(chairPersonRole);
		}
		// #37809 End Vinod 

		String userRole = null;
		String committeeType = getManagedBean().getTplCommittee().getCommitteeType().getParameterCode();
		List<TplCommMember> scCommMember = getManagedBean().getTplCommittee().getTplCommMembers();

		boolean hasChairperson = false;

		ICommitteSO committeeSO = null;
		try {
			committeeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmUser(pmUser);
			reqDTO.setCommitteeType(committeeType);

			if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
					|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {

				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO mofReqDTO = new OrgProfileReqDTO();
				
				//#35320 - Start
				mofReqDTO.setOrgCode(ParameterConst.MOF_ORG_CODE);
				OrgProfileResDTO mofResDTO = orgProfileSO.retrieveOrgProfileByCode(mofReqDTO);
				//#35320 - End
				
				reqDTO.setPmOrgProfile(mofResDTO.getPmOrgProfile());
			} else {
				PmOrgProfile pmOrgProfile = getManagedBean().getMinstryList().get(0);
				reqDTO.setPmOrgProfile(pmOrgProfile);
			}

			CommitteResDTO resDTO = committeeSO.checkIfUserHasRoleByRoleCodeCommTypeAndOrgProfile(reqDTO);
			if (resDTO.isSuccessful() && resDTO.isHasRequiredCommitteeRole()) {
				hasChairperson = isChairpersonExist(scCommMember);

				if (hasChairperson) {
					PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
					pmUser.setMemberRoleId(memberRole.getParameterId());
					userRole = getParameterDesc(memberRole);
				} else {
					PmParameter chairPersonRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
					pmUser.setMemberRoleId(chairPersonRole.getParameterId());
					userRole = getParameterDesc(chairPersonRole);
				}
			}
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION, null, null,
					"CommitteBackingBean.getTplRoleDesc", null, ex);
		}

		if (userRole == null) {
			userRole = getParameterDesc(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
		}

		return userRole;
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Checks if is member exists.
	 *
	 * @param existingMembers the existing members
	 * @param newMembers the new members
	 * @return true, if is member exists
	 */
	private boolean isTplMemberExists(List<TplCommMember> existingMembers, List<TplCommMember> newMembers) {
		if (!existingMembers.isEmpty()) {
			for (TplCommMember member : newMembers) {
				if (existingMembers.contains(member)) {
					return true;
				}
			}
		}
		return false;
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Delete committee action.
	 */
	public void deleteTplCommitteeAction() {

		List<TplCommMember> toBeDelCommitteMembers = getManagedBean().getToBeDelTplCommitteMembers();
		this.getManagedBean().setAutoUpdate(Boolean.TRUE);

		List<TplCommMember> committeeMemberList = this.getManagedBean().getTplCommittee().getTplCommMembers();
		List<TplCommMember> deleteList = new ArrayList<>();
		if (null == toBeDelCommitteMembers) {
			toBeDelCommitteMembers = new ArrayList<>();
			this.getManagedBean().setToBeDelTplCommitteMembers(toBeDelCommitteMembers);
		}

		for (int i = 0; i < committeeMemberList.size(); i++) {
			if (committeeMemberList.get(i).isSelected()) {
				committeeMemberList.get(i).setDeleted(true);
				deleteList.add(committeeMemberList.get(i));
			}
		}

		if (CollectionUtils.isNotEmpty(deleteList)) {
			committeeMemberList.removeAll(deleteList);
		}

		this.getManagedBean().setTplCommMembers(committeeMemberList);
		this.getManagedBean().setSelectedCommitteMemebers(null);
		toBeDelCommitteMembers.addAll(deleteList);
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Method to save and submit the data to BPM.
	 *
	 * @param outCome the out come
	 * @return the string
	 */
	public String updateTplCommitteDetails(String outCome) {

		CommitteReqDTO committeReqDTO = constructSaveTplCommitte();

		TplCommittee tplCommittee = getManagedBean().getTplCommittee();
		committeReqDTO.setTplCommittee(tplCommittee);
		committeReqDTO.setTplCommitteeId(tplCommittee.getCommitteeId());
		committeReqDTO.setOutcome(outCome);
		committeReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		committeReqDTO.setRoleCode(getTplCommitteeRoleCode());
		committeReqDTO.setMaintanceFlow(StringUtils.isEmpty(this.getManagedBean().getTaskName()));

		CommitteResDTO resDTO = null;
		ICommitteSO committeSo = null;

		try {
			committeSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					ICommitteSO.class);
			resDTO = committeSo.validateFcTplCommitteDetails(committeReqDTO);
			resDTO = committeSo.updateFCCommitteDetails(committeReqDTO);
			
			if (resDTO.isSuccessful()) {
				if (CollectionUtils.isNotEmpty(committeReqDTO.getToBeDeletedTplMembers())) {
					getManagedBean().setToBeDelTplCommitteMembers(null); 
				}

				getManagedBean().setRefreshFCMaintenancePage(Boolean.TRUE);
				getTabInfo();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_775201, new String[] {}, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				return NAV_TO_COMMITTEE_MAINTENANCE;
			}
		} catch(EpException epx) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							epx.getMessage(), epx.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
		} catch(Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION, null, null,
					"CommitteBackingBean.updateTplCommitteDetails", null, ex);
		} finally {
			committeSo = null;
		}
		return null;
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Construct save committe.
	 *
	 * @return the committe req dto
	 */
	private CommitteReqDTO constructSaveTplCommitte() {

		CommitteReqDTO committeeReqDTO = new CommitteReqDTO();

		// If a record is deleted and added back to the same committee,
		// remove the record from deleted members list
		List<TplCommMember> committeeMembers = getManagedBean().getTplCommMembers();
		List<TplCommMember> deletedCommitteeMembers = this.getManagedBean().getToBeDelTplCommitteMembers();
		List<TplCommMember> toBeAddedMembers = new ArrayList<>();
		
		if (CollectionUtils.isNotEmpty(deletedCommitteeMembers) && CollectionUtils.isNotEmpty(committeeMembers)) {
			Map<String, TplCommMember> currentCommMemberIcs = new HashMap<>();

			for (TplCommMember currCommMem : committeeMembers) {
				currentCommMemberIcs.put(currCommMem.getIcPassport(), currCommMem);
			}

			for (TplCommMember deletedCommitteeMember : deletedCommitteeMembers) {
				if (currentCommMemberIcs.containsKey(
						deletedCommitteeMember.getIcPassport())) {
					TplCommMember currentCommitteeMember = currentCommMemberIcs
											.get(deletedCommitteeMember.getIcPassport());
					
					deletedCommitteeMember = setTplCommitteeMemberData(deletedCommitteeMember, currentCommitteeMember);
					committeeMembers.remove(currentCommitteeMember);
					committeeMembers.add(deletedCommitteeMember);
					toBeAddedMembers.add(deletedCommitteeMember);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(toBeAddedMembers)) {
			deletedCommitteeMembers.removeAll(toBeAddedMembers);
		}
		if (CollectionUtils.isEmpty(deletedCommitteeMembers)) {
			getManagedBean().setToBeDelCommitteMemebers(null);
		}

		committeeReqDTO.setToBeDeletedTplMembers(getManagedBean().getToBeDelTplCommitteMembers());
		committeeReqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());

		FacesScopes facesScopes = new FacesScopes();
		committeeReqDTO.setUserLocale(facesScopes.getUserLocale());
		return committeeReqDTO;
	}
	// #35320 - END
	
	// #35320 - START
	private String getTplCommitteeRoleCode(){
		return getTplCommitteeMemberByUserId().getRoleCode();
	}
	// #35320 - END
	
	// #35320 - START
	private CommitteResDTO getTplCommitteeMemberByUserId() {
		
		FacesScopes facesScopes = new FacesScopes();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();	
		String committeeType = getManagedBean().getTplCommittee().getCommitteeType().getParameterCode();

		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeeReqDTO = new CommitteReqDTO();
		committeeReqDTO.setTplCommitteeId(this.getManagedBean().getTplCommittee().getCommitteeId());
		committeeReqDTO.setPmUser(curLogedInUser);
		committeeReqDTO.setCommitteeType(committeeType);
		return committeSO.retrieveTplCommitteMemberByUserId(committeeReqDTO);
	}
	// #35320 - END
	
	// #35320 - START
	/**
	 * Populate selected history and display the currently selected member
	 * history.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void onSelectTplHistory() throws IOException {
		
		TplCommMemberHistory history = this.getManagedBean().getSelectedTplCommHistory();
		List<QtCommitteeMemberBean> list = (List<QtCommitteeMemberBean>) (Object) history.deserializeList(history.getCommitteeMembers());
		CustomUtils.sortQtCommitteeMemberRole(list);
		list = sortMemberByAlphabetOrder(list);
		this.getManagedBean().setMemberHistoryPaginator(new Paginator(list));
		
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setTplCommitteeId(history.getTplCommittee().getCommitteeId());
		CommitteResDTO resDTO = this.getCommitteeSO().retrieveCommitteDtlByCommitteeId(reqDTO);
		history.setTplCommittee(resDTO.getTplCommittee());
	}
	// #35320 - END
	
	// #35320 - START
	private boolean isChairpersonExist(List<TplCommMember> scCommMember){
		boolean hasChairperson = false;
		for (TplCommMember commMember : scCommMember) {
			if (commMember.getMemberRole().getParameterCode().equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				hasChairperson = true;
				break;
			}
		}
		return hasChairperson;
	}
	// #35320 - END
	
	// #35320 - START
	private void setTplCommMemberRepresentativeType(List<TplCommMember> latestCommittee){
		for(TplCommMember mem : latestCommittee){
			if(mem.getRepresentativeType() != null){
				mem.setRepresentativeTypeId(mem.getRepresentativeType().getParameterId());
			}
		}
	}
	// #35320 - END
	
	// #37809 Start Vinod
	/**
	 * Gets the role list and construct for radio button.
	 * 
	 * @return an array of SelectItem
	 */
	public SelectItem[] getRoleListForAdd() {
		boolean chairpersonUpdate = isFcChairpersonUpdateView();

		SelectItem[] items = getDropdownValues(ParameterTypeConst.COMMITTEE_ROLE);

		List<SelectItem> tempList = new ArrayList<>();
		for (SelectItem item : items) {
			String role = (String) item.getLabel();
			boolean chairperson = StringUtils.equalsIgnoreCase(SourcingConstants.MEMBER_CHAIRPERSON, role);
			if (StringUtils.isNotEmpty(role)
					&& (chairperson || (!chairpersonUpdate && StringUtils.equalsIgnoreCase(
							SourcingConstants.MEMBER_ROLE, role)))) {
				tempList.add(item);
			}
			if (chairpersonUpdate && chairperson) {
				ScCommittee scCommittee = this.getManagedBean().getQtCommittee().getScCommittee();
				String committeeType = scCommittee.getCommitteeType().getParameterCode();
				Long roleId = Long.valueOf(item.getValue().toString());
				
				this.getManagedBean().getCommitteeSearchBean().setSearchRoleId(roleId);
				this.getManagedBean().setRoleCode(
						getRoleCodeWithCommitteAndRoleForSearch(committeeType, SourcingConstants.MEMBER_CHAIRPERSON));
			}
		}
		return tempList.toArray(new SelectItem[0]);
	}

	// #37809 End Vinod

	// #37809 Start Vinod
	public void searchRoleChangeListener(AjaxBehaviorEvent event) {
		Long searchRoleId = (Long) event.getComponent().getAttributes().get(QTConstants.TEMPLATE_CHANGE_VALUE);

		if (searchRoleId == null) {
			return;
		}
		String committeeType = resolveSearchCommitteeType();

		if (SourcingConstants.MEMBER_ROLE_ID_MEMBER.equals(searchRoleId)) {
			this.getManagedBean().setRoleCode(
					getRoleCodeWithCommitteAndRoleForSearch(committeeType, SourcingConstants.MEMBER_ROLE));
		} else {
			this.getManagedBean().setRoleCode(
					getRoleCodeWithCommitteAndRoleForSearch(committeeType, SourcingConstants.MEMBER_CHAIRPERSON));
		}

		this.getManagedBean().getCommitteeSearchBean().setSearchRoleId(searchRoleId);
	}

	// #37809 End Vinod

	// #37809 Start Vinod
	private String getRoleCodeWithCommitteAndRoleForSearch(String committeeType, String role) {
		StringBuilder roleCode = new StringBuilder();
		if (committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
				|| committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
			roleCode.append(StringUtils.equals(SourcingConstants.MEMBER_ROLE, role) ? RoleCode.FPB_MEMBER.value()
					: RoleCode.FPB_CHAIRPERSON.value());
		} else {
			roleCode.append(committeeType + "_" + role);
		}

		return roleCode.toString();
	}

	// #37809 End Vinod

	// #37809 Start Vinod 
	/**
	 * @return the committee type the member search runs against
	 */
	private String resolveSearchCommitteeType() {
		if (this.getManagedBean().getQtCommittee() != null
				&& this.getManagedBean().getQtCommittee().getScCommittee() != null) {
			return this.getManagedBean().getQtCommittee().getScCommittee().getCommitteeType().getParameterCode();
		}
		TplCommittee tplCommittee = getManagedBean().getTplCommittee();
		return tplCommittee != null && tplCommittee.getCommitteeType() != null ? tplCommittee.getCommitteeType()
				.getParameterCode() : null;
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * @return true for Federal Procurement Board Sabah or Sarawak
	 */
	private boolean isFpbSearchProfile() {
		String committeeType = resolveSearchCommitteeType();
		return ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equalsIgnoreCase(committeeType)
				|| ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equalsIgnoreCase(committeeType);
	}
	// #37809 End Vinod 

	// #37809 Start Vinod 
	/**
	 * Resets the member search criteria to the logged-in desk officer's PTJ.
	 */
	public void resetDeskOfficerSearchBean() {
		resetCommitteeSearchBean(deskOfficerProfileQt());
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * @return a search-only QT carrying the logged-in desk officer's PTJ, or
	 *         the current QT when the user has no PTJ
	 */
	private ScQt deskOfficerProfileQt() {
		PmOrgProfile deskOfficerPtj = getPTJOrgProfileByLoginUser();
		if (deskOfficerPtj == null) {
			return this.getManagedBean().getQt();
		}
		ScQt scQt = new ScQt();
		scQt.setOrgProfileId(deskOfficerPtj.getOrgProfileId());
		return scQt;
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * @return true when searching for members, whose ministry may be changed
	 */
	public boolean isMemberSearch() {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		return searchBean != null && SourcingConstants.MEMBER_ROLE_ID_MEMBER.equals(searchBean.getSearchRoleId());
	}
	// #37809 End Vinod 

	// #37809 Start Vinod
	public SelectItem[] getOrgCatList() {
		UserCommitteeSearchBean userCommitteeSearchBean = this.getManagedBean().getCommitteeSearchBean();
		Long roleId = userCommitteeSearchBean.getSearchRoleId();
		String lblPTJ = getMessageCommon(QTConstants.LBL_SC_COMMITTEE_ADD_MEMBER_SEARCH_PTJ_FULL);
		String lblOthers = getMessageCommon(QTConstants.LBL_SC_COMMITTEE_ADD_MEMBER_SEARCH_OTHER);
		List<SelectItem> items = new ArrayList<>();
		if (roleId == null || SourcingConstants.MEMBER_ROLE_ID_MEMBER.equals(roleId)) {
			items.add(new SelectItem(ParameterConst.ORG_CATEGORY_PTJ, lblPTJ));
			items.add(new SelectItem(ParameterConst.ORG_CATEGORY_OTHERS, lblOthers));
		} else {
			items.add(new SelectItem(ParameterConst.ORG_CATEGORY_PTJ, lblPTJ));
			userCommitteeSearchBean.setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		}

		return items.toArray(new SelectItem[0]);
	}

	// #37809 End Vinod

	// #37809 Start Vinod
	/**
	 * Populate user search criteria.
	 * 
	 * @param userCommitteeSearchBean
	 *            the user committee search bean
	 * @param userSearchBean
	 *            the user search bean
	 */
	private void populateOrganizationSearchCriteria(UserCommitteeSearchBean userCommitteeSearchBean,
			ISearchBean<PmUser> userSearchBean) {
		String orgCategory = null == userCommitteeSearchBean.getOrgCat() ? "" : userCommitteeSearchBean.getOrgCat();
		if (ParameterConst.ORG_CATEGORY_PTJ.equalsIgnoreCase(orgCategory)) {
			String ministry = userCommitteeSearchBean.getMinistry();
			if (StringUtils.isNotBlank(ministry)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
								.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
								SearchOperator.OPERATOR_EQUALS.value(), ministry, true));
			}
			String ptjGroup = userCommitteeSearchBean.getPtjGroup();
			if (StringUtils.isNotBlank(ptjGroup)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
								.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
								SearchOperator.OPERATOR_EQUALS.value(), ptjGroup, true));
			}
			String ptj = userCommitteeSearchBean.getPtj();
			if (StringUtils.isNotBlank(ptj)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
								.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DIVISION.value()),
								SearchOperator.OPERATOR_EQUALS.value(), ptj, true));
			}

		} else if (ParameterConst.ORG_CATEGORY_OTHERS.equalsIgnoreCase(orgCategory)) {
			String orgType = userCommitteeSearchBean.getOrgType();
			if (StringUtils.isNotBlank(orgType)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE_ORGTYPECODE_PARAMETERID
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.OTHERS.value()),
								SearchOperator.OPERATOR_EQUALS.value(), orgType, true));
			}
			String orgName = userCommitteeSearchBean.getOrgName();
			if (StringUtils.isNotBlank(orgName)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE_ORGPROFILEID
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.SUPPORT.value()),
								SearchOperator.OPERATOR_EQUALS.value(), orgName, true));
			}
		}
		
		String roleCode = getManagedBean().getRoleCode();
		if (StringUtils.isNotEmpty(roleCode)) {
			// Filter By - roleCode
			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMUSERROLES_PMROLE_ROLECODE,
							SearchOperator.OPERATOR_EQUALS.value(), roleCode));

			// Filter By - role status
			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMUSERROLES_RECORDSTATUSFLG,
							SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
		}
	}

	// #37809 End Vinod
	
	// #37809 Start Vinod
	private ScQt constructQTtoSearchDropdownValues(){
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);
		PmUser user = new FacesScopes().getUserSessionBean();
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		UserResDTO userResDTO1 = userSO.retrieveUserOrgListByUserId(reqDTO);
		List<PmUserOrg> userOrgs = userResDTO1.getUserOrgList();
		ScQt scQt = new ScQt();
		for (PmUserOrg pmUserOrg : userOrgs) {
			PmOrgProfile pmOrgProfile = pmUserOrg.getPmOrgProfile();

			if (pmOrgProfile != null && pmOrgProfile.getRecordStatusFlg() == 1) {
				scQt.setOrgProfileId(pmOrgProfile.getOrgProfileId());
				break;
			}
		}
		UserCommitteeSearchBean userCommitteeSearchBean = this.getManagedBean().getCommitteeSearchBean() != null ? this
				.getManagedBean().getCommitteeSearchBean() : new UserCommitteeSearchBean();
		if (StringUtils.isEmpty(userCommitteeSearchBean.getOrgCat())) {
			userCommitteeSearchBean.setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		}
		this.getManagedBean().setCommitteeSearchBean(userCommitteeSearchBean);
		
		return scQt;
	}
	
	// #37809 End Vinod

	// #37809 Start Vinod 
	
	private ScWorkFlowStatus retrieveStatusByTypeAndId(String docType, Long docId) {
		IScWorkFlowStatusSO workFlowStatusSO = null;
		try {

			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(docType);
			reqDTO.setDocId(docId);
			return workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO)
					.getWorkFlowStatus();
		} finally {
			workFlowStatusSO = null;
		}
	}

	// #37809 End Vinod

	// #37809 Start Vinod 
	/**
	 * @return true on the update finalization chairperson screen
	 */
	public boolean isFcChairpersonUpdateView() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext != null && facesContext.getViewRoot() != null
				&& NAV_TO_UPDATE_FC_CHAIRPERSON.equals(facesContext.getViewRoot().getViewId());
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * @param scCommittee
	 *            the finalization committee
	 * @return true when the logged-in user is the committee secretary
	 */
	private boolean isLoggedInUserSecretary(ScCommittee scCommittee) {
		Long loggedInUserId = getUserIdSessionFromLoginId();
		for (ScCommitteeMember member : scCommittee.getScCommitteeMembers()) {
			if (member.getMemberRole() != null
					&& ParameterConst.COMMITTEE_ROLE_SECRETARY.equals(member.getMemberRole().getParameterCode())
					&& member.getPmUser() != null && loggedInUserId != null
					&& loggedInUserId.equals(member.getPmUser().getUserId())) {
				return true;
			}
		}
		return false;
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * Opens the update finalization chairperson screen for the viewed QT.
	 *
	 * @return the navigation outcome
	 */
	public String updateFCChairpersonScreenAction() {
		ScCommittee scCommittee = getManagedBean().getQtCommittee().getScCommittee();
		String committeeType = scCommittee.getCommitteeType().getParameterCode();
		getManagedBean().setSelectedTab(committeeType);
		getManagedBean().setRefreshFCMaintenancePage(Boolean.TRUE);
		getTabInfo();

		TplCommittee tplCommittee = buildTplCommitteeFromQt(scCommittee);
		getManagedBean().setTplCommittee(tplCommittee);
		getManagedBean().setTplCommMembers(tplCommittee.getTplCommMembers());
		getManagedBean().setLatestMembers(tplCommittee.getTplCommMembers());
		getManagedBean().setToBeDelTplCommitteMembers(null);
		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setHeaderTitle(ParameterHelper.getInstance().getParameterDesc(
				ParameterTypeConst.SC_COMMITTEE_TYPE, committeeType));
		getManagedBean().getCommitteeSearchBean().setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		return NAV_TO_UPDATE_FC_CHAIRPERSON + "?faces-redirect=true";
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * Returns to the finalization committee view of the QT.
	 *
	 * @return the navigation outcome
	 */
	public String backFromUpdateFCChairpersonAction() {
		return editAction(getManagedBean().getQtCommittee());
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * Replaces the finalization chairperson of the QT and reassigns its
	 * sign-off task to the new chairperson.
	 *
	 * @return the navigation outcome
	 */
	public String updateFCChairpersonAction() {
		ScQtCommittee qtCommittee = getManagedBean().getQtCommittee();
		ScCommittee scCommittee = qtCommittee.getScCommittee();
		String committeeDesc = ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
				scCommittee.getCommitteeType().getParameterCode());
		FacesContext facesContext = FacesContext.getCurrentInstance();

		List<TplCommMember> chairpersons = new ArrayList<>();
		for (TplCommMember member : getManagedBean().getTplCommittee().getTplCommMembers()) {
			if (member.getMemberRole() != null
					&& ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equals(member.getMemberRole().getParameterCode())) {
				chairpersons.add(member);
			}
		}
		if (chairpersons.size() != 1) {
			String messageKey = chairpersons.isEmpty() ? MessageConst.ERROR_760711 : MessageConst.ERROR_760712;
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
					messageKey, new String[] { committeeDesc }, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			return null;
		}

		Long newChairpersonId = chairpersons.get(0).getPmUser().getUserId();
		for (ScCommitteeMember member : scCommittee.getScCommitteeMembers()) {
			if (member.getMemberRole() != null
					&& ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equals(member.getMemberRole().getParameterCode())
					&& newChairpersonId.equals(member.getPmUser().getUserId())) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						getMessageCommon(QTConstants.LBL_SC_UPDATE_SAME_COMMITTEE_MEMBER)));
				return null;
			}
		}

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setScQtCommittee(qtCommittee);
		reqDTO.setUserId(newChairpersonId);
		reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			committeSO.updateFinalizationChairperson(reqDTO);
		} catch (EpException epx) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
					epx.getMessage(), epx.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			return null;
		}

		editAction(qtCommittee);
		updateFCChairpersonScreenAction();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
				getMessageCommon(QTConstants.LBL_SC_COMMITTEE_CHAIRPERSON_UPDATE_SUCCESS)));
		return null;
	}

	// #37809 End Vinod

	// #37809 Start Vinod 

	/**
	 * Copies the QT finalization committee into a template committee so the
	 * maintenance table, add and delete actions can edit it.
	 *
	 * @param scCommittee
	 *            the QT finalization committee
	 * @return the template committee holding the QT members
	 */
	private TplCommittee buildTplCommitteeFromQt(ScCommittee scCommittee) {
		TplCommittee tplCommittee = new TplCommittee();
		tplCommittee.setCommitteeType(scCommittee.getCommitteeType());
		tplCommittee.setApprovalReq(scCommittee.isApprovalReq());
		tplCommittee.setCommitteStatus(scCommittee.getCommitteStatus());
		tplCommittee.setRemark(scCommittee.getRemark());
		tplCommittee.setRecordStatus(scCommittee.isRecordStatus());

		List<TplCommMember> tplCommMembers = new ArrayList<>();
		for (ScCommitteeMember scCommitteeMember : scCommittee.getScCommitteeMembers()) {
			TplCommMember tplCommMember = new TplCommMember();
			tplCommMember.setTplCommittee(tplCommittee);
			tplCommMember.setPmUser(scCommitteeMember.getPmUser());
			tplCommMember.setMemberName(scCommitteeMember.getMemberName());
			tplCommMember.setIcPassport(scCommitteeMember.getIcPassport());
			tplCommMember.setPmOrgProfile(scCommitteeMember.getPmOrgProfile());
			tplCommMember.setDesignation(scCommitteeMember.getDesignation());
			if (scCommitteeMember.getMemberRole() != null) {
				tplCommMember.setMemberRole(scCommitteeMember.getMemberRole());
				tplCommMember.setMemberRoleId(scCommitteeMember.getMemberRole().getParameterId());
			}
			tplCommMember.setAccessRight(scCommitteeMember.getAccessRight());
			tplCommMember.setIsEvaluateMember(scCommitteeMember.getIsEvaluateMember());
			tplCommMember.setConsolidate(scCommitteeMember.isConsolidate());
			tplCommMember.setPNP(scCommitteeMember.isPNP());
			tplCommMember.setEmail(scCommitteeMember.getEmail());
			if (scCommitteeMember.getRepresentativeType() != null) {
				tplCommMember.setRepresentativeType(scCommitteeMember.getRepresentativeType());
			}
			tplCommMember.setGrade(scCommitteeMember.getGrade());
			tplCommMember.setRepresentativeTypeId(scCommitteeMember.getRepresentativeTypeId());
			tplCommMembers.add(tplCommMember);
		}
		Collections.sort(tplCommMembers, new TplMemberRoleComparator());
		tplCommittee.setTplCommMembers(tplCommMembers);
		return tplCommittee;
	}
	// #37809 End Vinod 
}
