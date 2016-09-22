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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtAppRequestRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.loiloa.LOIDetailBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.scheduler.helper.sc.ScSchedulerHelper;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.QTFiledMapper;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.quartz.SchedulerException;

// TODO: Auto-generated Javadoc
/**
 * The Class QTDetailBackingBean.
 * 
 * @Author VidyasagarReddy
 */
@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);
	
	private static final int REFORM_NO_ACTION = 0;
	private static final int REFORM_SC = 1;
	private static final int REFORM_OC = 2;
	private static final int REFORM_TEC = 4;
	private static final int REFORM_FEC = 8;

	/**
	 * Instantiates a new QT detail backing bean.
	 */
	public QTDetailBackingBean() {
		log.info("inside constructor");
		log.info("@@@@@@@@@ Managed Bean @@@@@@@@@ = "+this.getManagedBean());

	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt!= null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}else{
			log.info(message);
		}
		
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init() {
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		if (null == qtnTenderSearchBean.getMinistryProfile()
				|| null == qtnTenderSearchBean.getPtjGroupProfile()
				|| null == qtnTenderSearchBean.getPtjProfile()) {
			getDefaultPtjAndMinistryValues();
		}
	}
	
	/**
	 * Gets the search qt data model.
	 * 
	 * @return the search qt data model
	 */
	public IPagingDataModel<ScQt> getSearchQTDataModel() {
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		if (searchQTDataModel == null) {
			searchQTDataModel = new QTPagingDataModel("searchQTs", getManagedBean()
					.getQtnTenderSearchBean());
			getManagedBean().setSearchQTDataModel(searchQTDataModel);
		}
		return searchQTDataModel;
	}
	
	private void renewQTSesionObject() {
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean()
				.getQtnTenderSearchBean();
		
		super.renewSesionObject();
		
		getManagedBean().setSearchQTDataModel(searchQTDataModel);
		getManagedBean().setQtnTenderSearchBean(qtnTenderSearchBean);
	}

	/**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		
		PmOrgProfile orgProfile = getOrgProfileByLoginUser();
		if (orgProfile != null) {
			String orgType = orgProfile.getOrgTypeCode().getParameterCode();
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				ministryProfile = orgProfile;
			} else if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
				ptjGroupProfile = orgProfile;
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
				ptjProfile = orgProfile;
				ptjGroupProfile = retrieveJabatanOrgProfile(
						orgProfile.getOrgProfileId());
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			}
		}
		
		qtnTenderSearchBean.setMinistryProfile(ministryProfile);
		qtnTenderSearchBean.setPtjGroupProfile(ptjGroupProfile);
		qtnTenderSearchBean.setPtjProfile(ptjProfile);
	}
	
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = facesScope.getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if (resDTO.getUserOrgList() != null
						&& resDTO.getUserOrgList().size() > 0) {
					for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
						if (pmUserOrg.getPmOrgProfile() != null) {
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}

	/**
	 * To add empty row to gosodo table.
	 */
	public void addGoSoDoAction() {
		debugLogger("addGoSoDoAction");
		List<ScQtBudget> gosodos = this.getManagedBean().getQt()
				.getScQtGosodoBudgets();
		if (gosodos == null) {
			gosodos = new ArrayList<ScQtBudget>();
			this.getManagedBean().getQt().setScQtGosodoBudgets(gosodos);
		}
		ScQtBudget scQtGosodoBudget = new ScQtBudget();
		scQtGosodoBudget.setPmGo(new PmGo());
		scQtGosodoBudget.setPmSo(new PmSo());
		scQtGosodoBudget.setScQt(this.getManagedBean().getQt());
		gosodos.add(scQtGosodoBudget);
	}

	/**
	 * Delete go so do action.
	 */
	public void deleteGoSoDoAction() {
		debugLogger("deleteGoSoDoAction");
		List<ScQtBudget> budgetList = getManagedBean().getQt()
				.getScQtGosodoBudgets();
		List<ScQtBudget> selectedBudgetList = this.getManagedBean()
				.getSelectedGosodoBudgets();
		if (budgetList.size() == selectedBudgetList.size()) {
			budgetList.clear();
		} else {
			for (ScQtBudget scQtBudget : selectedBudgetList) {
				budgetList.remove(scQtBudget);
			}
		}
	}

	/*
	 * Method to delete gosodo from table
	 */
	/**
	 * Delete go so do item.
	 * 
	 * @param changeEvent
	 *            the change event
	 */
	public void deleteGoSoDoItem(ValueChangeEvent changeEvent) {
		ScQtBudget gosodoBudget = (ScQtBudget) changeEvent.getNewValue();
		List<ScQtBudget> gosodos = this.getManagedBean().getGosodos();
		if (gosodos.contains(gosodoBudget)) {
			gosodos.remove(gosodoBudget);
		}
	}

	/*
	 * Onchange of go's to fetch the so's list
	 */
	/**
	 * Handle go changed.
	 * 
	 * @param qtGosodoBudget
	 *            the qt gosodo budget
	 * @return the list
	 */
	public List<PmSo> handleGoChanged(ScQtBudget qtGosodoBudget) {
		debugLogger("handleGoChanged");
		PmGo pmGo = qtGosodoBudget.getPmGo();
		if (pmGo != null && pmGo.getGoId() != null) {
			List<PmGo> pmGoList = this.getManagedBean().getGoList();
			for (PmGo go : pmGoList) {
				if (go.getGoId().longValue() == pmGo.getGoId().longValue()) {
					qtGosodoBudget.setPmGo(go);
					debugLogger("set GO");
					break;
				}
			}
			// get the so list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setGoId(pmGo.getGoId());
			IObjectSO objectSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			this.getManagedBean().setSoList(objResDTO.getPmSoList());
			return objResDTO.getPmSoList();
		} else {
			this.getManagedBean().setSoList(new ArrayList<PmSo>());
		}
		// qtGosodoBudget.setPmSo(new PmSo());
		// handleSoChanged(qtGosodoBudget);
		return null;
	}

	/*
	 * OnChange of So's get the do's list
	 */
	/**
	 * Handle so changed.
	 * 
	 * @param qtGosodoBudget
	 *            the qt gosodo budget
	 */
	public void handleSoChanged(ScQtBudget qtGosodoBudget) {
		debugLogger("handleSoChanged");
		PmSo pmSo = qtGosodoBudget.getPmSo();
		if (pmSo != null && pmSo.getSoId() != null) {
			List<PmSo> pmSoList = this.getManagedBean().getSoList();
			for (PmSo so : pmSoList) {
				if (so.getSoId().longValue() == pmSo.getSoId().longValue()) {
					qtGosodoBudget.setPmSo(so);
					debugLogger("set SO");
					break;
				}
			}
			// get the do list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setSoId(pmSo.getSoId());
			IObjectSO objectSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveDOListBySOCode(reqDTO);
			this.getManagedBean().setDoList(objResDTO.getPmDoList());
		} else {
			this.getManagedBean().setDoList(new ArrayList<PmDo>());
		}
	}

	/**
	 * Handle do change.
	 * 
	 * @param qtGosodoBudget
	 *            the qt gosodo budget
	 */
	public void handleDoChange(ScQtBudget qtGosodoBudget) {

	}

	/**
	 * Method to save the QT details.
	 */
	public void saveQtDetailsAction() {
		
		debugLogger("start of saveQtDetailsAction");
		this.getManagedBean().setSaveFlow(true);
		try {
			validateQtDetails();
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
															getParameterDesc(
																	ParameterTypeConst.DOCUMENT_TYPES,
																	ParameterConst.DOCTYPE_QUOTATION),
															this.getManagedBean()
																	.getQt()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			debugLogger("end of saveQtDetailsAction");
		} finally {

		}
		
	}

	/**
	 * Submit qt details action.
	 * 
	 * @return the string
	 */
	public String submitQTDetailsAction() {
		
		debugLogger("start of submitQTDetailsAction");
		
		this.getManagedBean().setSaveFlow(false);
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			updateApprovalDetails(this.getManagedBean().getQt());
			//Required to fetch the approvers when are trying to Save/Submit from other than the Approval tab 
			retrieveApprovalTabDtl();
			
			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setSaveFlow(false);
			FacesScopes facesScopes = new FacesScopes();
			quationTenderReqDTO.setToken(facesScopes
					.getBPMAuthenticationCtxToken());

			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			quationTenderReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			quationTenderReqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
			quationTenderReqDTO.setUserLocale(facesScopes.getUserLocale());
			quationTenderReqDTO.getQtdetail().setOtherApproval(true);
			quationTenderReqDTO.setDeskOfficer(facesScopes.getUserSessionBean());
			QuationTenderResDTO quationTenderResDTO = detailSO
					.submitQT(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {
				ScQt scQt = quationTenderResDTO.getQtDetail();
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
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				debugLogger("end of submitQTDetailsAction");
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	/**
	 * Method to search the quationtenders based on serach date.
	 */
	public void searchQTAction() {
		getManagedBean().getSearchQTDataModel().reloadData();
	}

	/**
	 * Method for clone search qts.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	/**
	 * Gets the go list.
	 * 
	 * @return the go list
	 */
	public List<PmGo> getGoList() {
		debugLogger("getGoList");
		List<PmGo> goList = this.getManagedBean().getGoList();
		if (goList == null) {
			IObjectSO objectSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveGOList();
			goList = objResDTO.getPmGoList();
			this.getManagedBean().setGoList(goList);
		}
		return goList;
	}

	/**
	 * Handle ptj name changed.
	 * 
	 */
	public void handlePTJNameChanged() {
		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			ScQt scQT = getManagedBean().getQt();

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(scQT.getPmOrgProfile().getOrgProfileId());

			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileDtlByProfileId(reqDTO);

			scQT.setPmOrgProfile(resDTO.getPmOrgProfile());

		} finally {
			orgProfileSO = null;
		}
	}

	/**
	 * Goto qt search page.
	 * 
	 * @return the string
	 */
	public String gotoQtSearchPage() {
		debugLogger("gotoQtSearchPage");

		return QTConstants.SEARCH_PAGE;
	}

	/**
	 * Method to return the maintance page.
	 * 
	 * @param scQt
	 *            the sc qt
	 * @return maintanacepage
	 */
	public String editQuationTenderDetails(ScQt scQt, boolean editable) {
		
		debugLogger("start editQuationTenderDetails");
		renewQTSesionObject();
		debugLogger("editQuationTenderDetails, @Managedbean = "+ this.getManagedBean());
		this.getManagedBean().setReadOnly(!editable);
		this.getManagedBean().setMaintanceFlow(true);
		IQTDetailSO detailSO;
		String REDIRECT_PAGE = QTConstants.MAINTANANCE_PAGE;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			Long qtId = scQt.getQtId();

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setMaintanceFlow(true);
			reqDTO.setQtId(qtId);
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			ScQt selectedScQt = resDTO.getQtDetail();		

			if (null == selectedScQt.getContractType()) {
				selectedScQt.setContractType(new PmParameter());
			}
			if (null == selectedScQt.getProcurementTypeCatId()) {
				selectedScQt.setProcurementTypeCatId(new PmParameter());
			}
			if (selectedScQt.isPanel() && null == selectedScQt.getItemGroup()) {
				selectedScQt.setItemGroup(new CtPanelItemGroup());
			}
			if(selectedScQt.getFulfilmentType()==null){
				selectedScQt.setFulfilmentType(new PmParameter());
			}
			if(selectedScQt.getPmProcurementCategory()==null){
				selectedScQt.setPmProcurementCategory(new PmParameter());
			}	
			this.getManagedBean().getCtContract()
					.setContractNo(selectedScQt.getExistingContractNo());
			
			setAmmendMentBean(resDTO.getAmendMentBean());

			Long currentStatus = selectedScQt.getQtStatus().getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);
			this.getManagedBean().setQt(selectedScQt);

			if ( QTStatusConst.QT_CANCELLED.equals(currentStatus) && null == selectedScQt
							.getQtType()) {
				ScQtCommittee committee = getQtCommiteDtlsByQtIdNType("BO",
						selectedScQt.getQtId());
				List<ScQtCommittee> qtCommittee = new ArrayList<ScQtCommittee>();
				qtCommittee.add(committee);
				// selectedScQt.setScQtCommittees(qtCommittee);
				this.getManagedBean().setScQtCommittee(committee);

				List<ScCommitteeMember> committeMembers = committee
						.getScCommittee().getScCommitteeMembers();
				// Code for selected consolidate Person should be highlight..
				if (null != committeMembers) {
					for (ScCommitteeMember committeeMember : committeMembers) {
						if (committeeMember.isConsolidate()) {
							this.getManagedBean().setSelectScCommitteMember(
									committeeMember);
							break;
						}
					}
				}
				this.getManagedBean().setScCommitteeMemberList(committeMembers);
				this.getManagedBean()
						.setCommitteMember(new ScCommitteeMember());

			} else {

				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (selectedScQt.isZonal()) {
						// Populate zone detail into tree node
						if (selectedScQt.getScQtSupplierCriteria() != null &&
								selectedScQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
							List<TplSpecZoneDtl> specZoneDtls = selectedScQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
							populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
						}
					}
				}
				this.getManagedBean().setGoList(resDTO.getGoList());

			}
			if (null != getManagedBean().getQt()
					&& null == getManagedBean().getQt()
							.getProcurementTypeCatId()) {
				getManagedBean().getQt().setProcurementTypeCatId(
						new PmParameter());
			}

			setScQtTrackingDairyObject(scQt);
			if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean()
					.getCurrentStatusId())) {
				this.getManagedBean().setCreateQt(true);
			}
			debugLogger("end of editQuationTenderDetails");
					} finally {
			detailSO = null;
		}
		return REDIRECT_PAGE;
	}
	
	/**
	 * Method to view Direct LOA Details.
	 * 
	 * @param scQt
	 *            the ScQt instance
	 * @param previousPageUrl
	 * 				the previous page url.
	 * @return the navigation page
	 */
	public String viewDirectLOADetails(ScQt scQt, String previousPageUrl) {
		debugLogger("viewDirectLOADetails");
		
		Long qtId = scQt.getQtId();
		Long loaId = findDocIdByDocType(qtId, ParameterConst.DOCTYPE_DIRECT_NEGO);
		
		return new DirectLOABackingBean().retriveLoaDetails(loaId, previousPageUrl, true);
	}
	
	/**
	 * Method to view Supplier Proposal Details.
	 * 
	 * @param scQt
	 *            the ScQt instance
	 * @param previousPageUrl
	 * 				the previous page url.
	 * @return the navigation page
	 */
	public String viewSupplierProposalDetails(ScQt scQt, String previousPageUrl) {
		
		debugLogger("viewSupplierProposalDetails");
		Long qtId = scQt.getQtId();
		String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		
		if (ParameterConst.DOCTYPE_QUOTATION.equals(docType) || 
				ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL.equals(docType)) {
			return new SPDetailBackingBean().retriveSupplierProposalDtl(qtId, previousPageUrl, true);
		} else if (ParameterConst.DOCTYPE_BIDDING.equals(docType)) {
			return QTConstants.ONLINE_BIDDING_MAIN_PAGE; 
		} else {
			Long docId = findDocIdByDocType(qtId, null);			
			if (docId != null) {
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
					return new LOIDetailBackingBean().retrieveSupplierLOIDtl(
							qtId, docId, previousPageUrl, true);
				} else if (ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)) {
					return new LOADetailBackingBean().retrieveSupplierLOADtl(
							qtId, docId, previousPageUrl, true);
				}	
			}
		}
		
		return null;
	}
	
	/**
	 * Find Document Id by document type
	 * 
	 * @param qtId
	 * 		the quotation / tender id
	 * @return	the document id
	 */
	public Long findDocIdByDocType(Long qtId, String docType) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			
			QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
			populateQTSearchCriteria(reqDTO, qtnTenderSearchBean, new SearchBean<ScQt>());
			if (StringUtils.isNotEmpty(docType)) {
				reqDTO.setDocType(docType);
				reqDTO.setDocNo(null);
			}
			return qtDetailSO.findDocumentNoByDocType(reqDTO);
		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Method to prepare scqt object with all dependent object.
	 * 
	 * @return QTDetailReq object
	 */
	public QuationTenderReqDTO prepareQTInfo() {
		debugLogger("prepareQTInfo");
		log.info("Menu Index : " + this.getManagedBean().getActiveIndex());
		ScQt scQt = this.getManagedBean().getQt();
		// QT number need to be generated by code it should be unique
		if (scQt.isBrandNew()) {
			scQt.setQtNo(getManagedBean().getQt().getQtNo());
		}
		if (!scQt.getSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}

		scQt.setExistingContractNo(this.getManagedBean().getCtContract()
				.getContractNo());

		// Setting the Committee Appointment details to Qtdetails
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommitteList = null;
		if (CollectionUtils.isNotEmpty(committeTabs)) {
			qtCommitteList = new ArrayList<ScQtCommittee>();
			for (int i = 0; i < committeTabs.size(); i++) {
				TabBean tabBean = committeTabs.get(i);
				if (!SourcingConstants.COMMITTE_TYPE_FINALIZATION
						.equals(tabBean.getTabType())) {
					ScQtCommittee qtCommittee = tabBean.getScQtCommittee();
					
					qtCommittee.setScQt(scQt);	// set latest saved scQt object.
					qtCommitteList.add(qtCommittee);
				}
			}
		}
		scQt.setScQtCommittees(qtCommitteList);
		// Code for to update the supplier qualification criteria
		/* List<ScQtCategory> categoryList = new ArrayList<ScQtCategory>(); */
		if (!this.getManagedBean().getQt().isPanel()) {
			if (this.getManagedBean().getQt().getQtType()
					.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				// TODO need to check why updating the twice.. sagar
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (CollectionUtils.isNotEmpty(openList)) {
					/*
					 * for (ScQtCategory scQtCategory : openList) { if (null !=
					 * scQtCategory.getPmCategoryL3()) {
					 * scQtCategory.setPmCategoryL1(scQtCategory
					 * .getPmCategoryL3().getPmCategoryL2() .getPmCategoryL1());
					 * scQtCategory.setPmCategoryL2(scQtCategory
					 * .getPmCategoryL3().getPmCategoryL2()); //
					 * scQtCategory.setScQt(this.getManagedBean().getQt());
					 * setUserDetails(scQtCategory);
					 * categoryList.add(scQtCategory); } }
					 */
					scQt.setScQtCategorys(openList);
				}
			} else if (this.getManagedBean().getQt().getQtType()
					.equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)) {
				List<ScQtCategory> restrictedList = this.getManagedBean()
						.getQtCategoryList();
				if (CollectionUtils.isNotEmpty(restrictedList)) {

					scQt.setScQtCategorys(restrictedList);
				}
				// Setting the Restricted Supplier List to Qtdetails
				List<ScQtSupplier> qtSupplierList = this.getManagedBean()
						.getQtSupplierList();
				if (qtSupplierList != null && !qtSupplierList.isEmpty()) {
					for (ScQtSupplier scQtSupplier : qtSupplierList) {
						scQtSupplier
								.setInvitationType(SourcingConstants.INVITATION_TYPE_INSERT);
						setUserDetails(scQtSupplier);
						// qtSupplierList.add(scQtSupplier);
					}
					scQt.setScQtSuppliers(qtSupplierList);
				}

			}
		}
		
		if (scQt.getScQtSupplierCriteria() != null
				&& !scQt.getScQtSupplierCriteria().isAllState()) {
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) convertTreeNodesToLocationCriteria(
					this.getManagedBean().getZoneTrees(),scQt.getScQtSupplierCriteria());
					
			/*List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), scQt.getScQtSupplierCriteria());*/
			if (null != locCriteria && !locCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
			} /*else {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
			}*/
		}
		if (scQt.isZonal()) {
			if (null != scQt.getScQtSupplierCriteria() && scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
				ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
				ScQtZoneCriteria qtZoneCriteria = supplierCriteria.getQtZoneCriterias();
				
				if (qtZoneCriteria.isBrandNew()) {
					qtZoneCriteria.setScQtSupplierCriteria(supplierCriteria);
					setUserDetails(qtZoneCriteria);	
				}
				
				List<TreeNode> zoneTrees = this.getManagedBean().getZoneSuppQualTrees();
				if (zoneTrees == null) {
					zoneTrees = this.getManagedBean().getZoneGeneralTrees();
				}
				
				List<TplSpecZoneDtl> specZoneDtls = convertTreeNodesToZoneCriteria(
						zoneTrees, null);

				if (specZoneDtls != null && !specZoneDtls.isEmpty()) {					
					for (TplSpecZoneDtl tplSpecZoneDtl : specZoneDtls) {
						tplSpecZoneDtl.setTplSpecZone(qtZoneCriteria);
					}
					qtZoneCriteria.setTplSpecZoneDtls(specZoneDtls);
				} else {
					qtZoneCriteria.setTplSpecZoneDtls(null);
				}
			}
		}
		
		// Setting the Approval details to Qtdetails
		List<TabBean> tabList = this.getManagedBean().getTabList();

		if (CollectionUtils.isNotEmpty(tabList)) {
			List<ScQtApproval> approvalList = null;
			approvalList = new ArrayList<ScQtApproval>();
			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(
							tabBean.getBudgetApprovalUsers().getTarget());
				}

				if (this.getManagedBean().getQt().isBrandNew()) {
					tabBean.getScQtApproval().setScQt(
							this.getManagedBean().getQt());
				}
				// setUserDetails(tabBean.getScQtApproval());

				approvalList.add(tabBean.getScQtApproval());

			}
			scQt.setScQtApprovals(approvalList);
		}
		if (CollectionUtils.isNotEmpty(scQt.getScQtApprovals())) {
			for (ScQtApproval qtApproval : scQt.getScQtApprovals()) {
				qtApproval.setScQtApprovalRemarks(
						finalizeRemarks(qtApproval
								.getScQtApprovalRemarks()));
			}
		}

		scQt.setScQtBriefings(null);
		if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())) {
			// Setting the qtBreifing details to Qtdetails
			List<BriefingZoneBean> beansList = this.getManagedBean()
					.getBriefingZoneBean();
			List<ScQtBsv> bsvs = null;
			if (beansList != null && !beansList.isEmpty()) {
				bsvs = new ArrayList<ScQtBsv>();
				for (BriefingZoneBean bsvDtlBean : beansList) {
					ScQtBsv scQtBsv = constructBsvByBean(bsvDtlBean);
					scQtBsv.setBsvRemarks(finalizeRemarks(scQtBsv
							.getBsvRemarks()));
					// scQtBsv.setScQt(this.getManagedBean().getQt());
					scQtBsv.setTplSpecZoneDtl(bsvDtlBean.getTplSpecZoneDtl());
					if (null != scQtBsv) {
						bsvs.add(scQtBsv);
					}
				}

				for (int i = 0; i < bsvs.size(); i++) {
					List<ScQtBsvDtl> bsvDtls = bsvs.get(i).getScQtBsvDtls();
					if (bsvDtls == null || !(bsvDtls.size() > 0)) {
						bsvs.remove(i--);
					}
				}

			} else {
				// code for if bsv details if any and non zonal
				BriefingZoneBean bsvDtlBean = this.getManagedBean()
						.getBsvDtlBean();
				ScQtBsv scQtBsv = constructBsvByBean(bsvDtlBean);

				if (null != scQtBsv) {
					scQtBsv.setBsvRemarks(finalizeRemarks(scQtBsv
							.getBsvRemarks()));
					// scQtBsv.setScQt(this.getManagedBean().getQt());
					bsvs = new ArrayList<ScQtBsv>();
					bsvs.add(scQtBsv);
				}

			}

			scQt.setScQtBriefings(bsvs);

			// Setting Sample Submission to qtDetails.
			List<ScQtSampleSubmission> sampleSubmissionList = null;
			if (scQt.isZonal()) {
				sampleSubmissionList = getManagedBean()
						.getScQtSampleSubmissionList();
				if (sampleSubmissionList != null
						&& !sampleSubmissionList.isEmpty()) {
					for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
						ScAddress scAddress = scQtSampleSubmission.getScAddress();

						if (null != scAddress) {
							if (scAddress.getPmDivision() != null
									&& scAddress.getPmDivision()
											.getDivisionId() == null) {
								scAddress.setPmDivision(null);
							}
							scAddress.setPmCountry(retrieveCountry());
						}
						scQtSampleSubmission.setScQt(getManagedBean().getQt());
					}
				}
			} else {
				sampleSubmissionList = new ArrayList<ScQtSampleSubmission>();
				if (null != this.getManagedBean().getScQtSampleSubmission()) {
					ScAddress scAddress = this.getManagedBean()
							.getScQtSampleSubmission().getScAddress();

					if (scAddress.getPmDivision() != null
							&& scAddress.getPmDivision().getDivisionId() == null) {
						scAddress.setPmDivision(null);
					}
					scAddress.setPmCountry(retrieveCountry());
					sampleSubmissionList.add(this.getManagedBean()
							.getScQtSampleSubmission());
				}
			}
			scQt.setScQtSampleSubmission(sampleSubmissionList);

			// Setting the contact person to qtdetails.
			List<ScQtContactPerson> contactPersonsList = this.getManagedBean()
					.getScQtContactPersons();
			List<ScQtContactPerson> deskOfficerAdminList = this
					.getManagedBean().getPmuserContactpersonList();
			List<ScQtContactPerson> contactList = new ArrayList<ScQtContactPerson>();

			if (deskOfficerAdminList != null && !deskOfficerAdminList.isEmpty()) {
				// contactPersonsList.addAll(deskOfficerAdminList);
				contactList.addAll(deskOfficerAdminList);
			}
			if (contactPersonsList != null && !contactPersonsList.isEmpty()) {
				contactList.addAll(contactPersonsList);
			}
			if (null != contactPersonsList) {
				scQt.setScQtContactPersons(contactList);
			}

			ScQtApproval pubApproval = this.getManagedBean()
					.getPublicationApproval();
			if (null != pubApproval) {
				List<ScQtApproval> approvals = scQt.getScQtApprovals();
				removePTJApproval(approvals);
				if (null != this.getManagedBean().getPublicationApprovals()) {
					List<ScQtApproverSettings> approvalSettings = this
							.getManagedBean().getPublicationApprovals()
							.getTarget();
					if (CollectionUtils.isNotEmpty(approvalSettings)) {
						for (ScQtApproverSettings approverSettings : approvalSettings) {
							approverSettings.setScQtApproval(pubApproval);
						}
						pubApproval.setApproverSettings(approvalSettings);
					}

				}
				List<ScQtApprovalRemark> remarks = pubApproval
						.getScQtApprovalRemarks();
				finalizeRemarks(remarks);
				approvals.add(pubApproval);
				scQt.setScQtApprovals(approvals);
			}

		}
		scQt.setScQtStatus(null);
		
		// Setting the Deductions to qtdetails.
		List<ScQtDeduction> deductionList = this.getManagedBean().getScQtDeductionList();
		if (null != deductionList && !deductionList.isEmpty()) {
			for (ScQtDeduction deduction : deductionList) {
				deduction.setScQt(this.getManagedBean().getQt());
			}
			scQt.setScQtDeductions(deductionList);
		 }
		
		// Setting the Documents to qtdetails.
		List<ScQtDocReport> docReportsList = getManagedBean()
				.getScQtDocReportList();
		if (docReportsList != null && !docReportsList.isEmpty()) {
			scQt.setScQtDocReports(docReportsList);
		}
		
		// save.
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);
		quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

		quationTenderReqDTO.setToBeRemoveList(this.getManagedBean()
				.getToBeApprovalRemoveList());

		List<ScQtDeduction> deductionRemoveList = this.getManagedBean()
				.getRemoveDeductions();
		if (null != deductionRemoveList && !deductionRemoveList.isEmpty()) {
			for (int i = 0; i < deductionRemoveList.size(); i++) {
				if (null == deductionRemoveList.get(i).getQtDeductionId()) {
					deductionRemoveList.remove(i);
				}
			}
		}
		quationTenderReqDTO.setRemoveDeductions(deductionRemoveList);
		quationTenderReqDTO.setRemoveDocReports(getToBeRemoveList());

		List<ScQtLocCriteria> qtLocCriteriaRemoveList = this.getManagedBean()
				.getSupplierLocationList();
		if (null != qtLocCriteriaRemoveList
				&& !qtLocCriteriaRemoveList.isEmpty()) {
			for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
				criteria.setDeleted(true);
			}
		}
		quationTenderReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean().getToBeRemoveRestrictedSupplierList());
		quationTenderReqDTO
				.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);

		quationTenderReqDTO.setAmendMentBean(this.getManagedBean()
				.getAmendMentBean());

		quationTenderReqDTO.setToBeRemovedCommitteMembers(this.getManagedBean()
				.getToBeDelCommitteMemebers());

		List<ScQtCategory> removeCategoryCodes = this.getManagedBean()
				.getTobeRemoveCategoryCodes();
		if (null != removeCategoryCodes && !removeCategoryCodes.isEmpty()) {
			for (int i = 0; i < removeCategoryCodes.size(); i++) {
				if (null == removeCategoryCodes.get(i).getQtCategoryId()) {
					removeCategoryCodes.remove(removeCategoryCodes.get(i));
				}
			}
		}
		quationTenderReqDTO.setTobeRemoveCategoryCodes(removeCategoryCodes);
		quationTenderReqDTO.setMaintanceFlow(this.getManagedBean()
				.isMaintanceFlow());
		quationTenderReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		quationTenderReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		
		return quationTenderReqDTO;
	}

	/**
	 * @param approvals
	 */
	private void removePTJApproval(List<ScQtApproval> approvals) {
		if (CollectionUtils.isNotEmpty(approvals)) {
			for (int i = 0; i < approvals.size(); i++) {
				if (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approvals.get(i)
								.getApprovalCategory().getParameterCode())) {
					approvals.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * Gets the to be remove list.
	 * 
	 * @return the to be remove list
	 */
	private List<ScQtDocReport> getToBeRemoveList() {
		List<ScQtDocReport> toBeRemoveList = new ArrayList<ScQtDocReport>();

		List<ScQtDocReport> techList = getManagedBean()
				.getMarkDeletedScQtDocReportList();
		if (techList != null && techList.size() > 0) {
			toBeRemoveList.addAll(techList);
		}
		return toBeRemoveList;
	}

	/**
	 * Retrieve country.
	 * 
	 * @return the pm country
	 */
	private PmCountry retrieveCountry() {
		ICountrySO countrySo = null;
		PmCountry country = null;
		try {
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			country = countryResDTO.getPmCountry();
		} finally {
			countrySo = null;
		}
		return country;
	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param qtnTenderSearchBean
	 *            the qt search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 */
	private void populateQTSearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (qtnTenderSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean
					.getDataFilterBeans())) {
				qtSearchBean.getDataFilterBeans().clear();
			}
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				searchFilterBean.add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			
			// docType & docNo
			String docType = qtnTenderSearchBean.getDocType();
			String docNo = qtnTenderSearchBean.getDocNo();
			if (StringUtils.isNotEmpty(docType)) {
				if (ParameterConst.DOCTYPE_QUOTATION.equals(docType)) {
					if (!StringUtils.isBlank(docNo)) {
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField(QTConstants.QT_NUMBER);
						bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
						bean.setSearchValue(docNo);
						searchFilterBean.add(bean);	
					}
				} else {
					reqDTO.setDocType(docType);
					reqDTO.setDocNo(docNo);
				}
			}
			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				searchFilterBean.add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				if (!isQTMaintenanceScreen()) {
					procurementModes.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
				}
				searchFilterBean.add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
			}
			
			Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = qtnTenderSearchBean.getToCreatedDate();
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
			Date fromPublishDate = qtnTenderSearchBean.getFromPublishDate();
			Date toPublishDate = qtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromPublishDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromPublishDate = fromDate.getTime();
				if (toPublishDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toPublishDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toPublishDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromClosingDate = qtnTenderSearchBean.getFromClosingDate();
			Date toClosingDate = qtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			
			String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				searchFilterBean.add(new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				searchFilterBean.add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			
			if (isMofSupplier()) {
				// Filter By - supplierId
				ISupplierSearchSO supplierSearchSO = null;
				try {
					supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(userId);
					List<SmSupplier> smSuppliers = supplierSearchSO
							.retriveSupplierProfiles(supplierSearchReqDTO)
							.getResultBean().getResultList();
					
					if (CollectionUtils.isNotEmpty(smSuppliers)) {
						List<Long> supplierIds = new ArrayList<Long>();
						for (SmSupplier smSupplier : smSuppliers) {
							supplierIds.add(smSupplier.getSupplierId());
						}
						
						searchFilterBean.add(new SearchFilterBean(
								"scQtSuppliers.smSupplier.supplierId",
								SearchOperator.OPERATOR_IN.value(), 
								supplierIds));
						reqDTO.setSupplierIds(supplierIds);
					}

				} finally {
					supplierSearchSO = null;
				}
			} else {
				if (isDeskOfficer()) {
					// Filter By - actor user id
					reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
					
					// Only DO Actor with edit access right. 
					// Otherwise (Audit Search), user can view QT based on user roles match with actor.
					if (isQTMaintenanceScreen()) {
						reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
					}
				}
				
				PmOrgProfile ptjProfiles = qtnTenderSearchBean.getPtjProfile();
				if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
					// Filter By - PTJ
					searchFilterBean.add(new SearchFilterBean(
							"pmOrgProfile." + OrgProfileUtil
								.getSearchBeanParentString(OrganizationType.DIVISION.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ptjProfiles.getOrgProfileId()));
				} else {
					// Filter By - PTJ Group
					PmOrgProfile ptjGroupProfile = qtnTenderSearchBean.getPtjGroupProfile();
					if (ptjGroupProfile != null && ptjGroupProfile.getOrgProfileId() != null) {
						searchFilterBean.add(new SearchFilterBean(
								"pmOrgProfile." + OrgProfileUtil
									.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ptjGroupProfile.getOrgProfileId()));
					} else {
						// Filter By - Ministry
						PmOrgProfile ministryProfile = qtnTenderSearchBean.getMinistryProfile();
						if (ministryProfile != null
								&& ministryProfile.getOrgProfileId() != null) {	
							searchFilterBean.add(new SearchFilterBean(
									"pmOrgProfile." + OrgProfileUtil
										.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
									SearchOperator.OPERATOR_EQUALS.value(),
									ministryProfile.getOrgProfileId()));
						}
					}
					
				}
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			
			reqDTO.setQtSearchBean(qtSearchBean);
		}
	}
	
	private boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}

	/**
	 * Retrive financial specification.
	 */
	public void retriveFinancialSpecification() {
		debugLogger("retriveFinancialSpecification");
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			reqDTO.setPrepareSpec(true);

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto.getFinSpecResultBean().getResultList() != null
					&& !resDto.getFinSpecResultBean().getResultList().isEmpty()) {
				this.getManagedBean().setFinancialSpecList(
						resDto.getFinSpecResultBean().getResultList());
			}
		} finally {
			specSO = null;
		}
	}

	/**
	 * Retrive technical specification.
	 */
	public void retriveTechnicalSpecification() {
		debugLogger("retriveTechnicalSpecification");
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			reqDTO.setPrepareSpec(true);

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto.getSpecResultBean().getResultList() != null
					&& !resDto.getSpecResultBean().getResultList().isEmpty()) {
				this.getManagedBean().setTechnicalSpecList(
						resDto.getSpecResultBean().getResultList());
			}
		} finally {
			specSO = null;
		}
	}

	/**
	 * Retrieve briefing sitevisit tab dtl.
	 */
	public void retrieveBriefingSitevisitTabDtl() {
		
		ScQt qt = this.getManagedBean().getQt();
		debugLogger("retrieveBriefingSitevisitTabDtl");

		if (this.getManagedBean().getBsvDtlBean() == null
				&& this.getManagedBean().getBriefingZoneBean().isEmpty()) {
			IQTBriefingSO qTBriefingSO = null;
			try {
				qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTBriefingSO.class);
				String languageCode = new FacesScopes().getUserLocale()
						.getLanguage();
				BriefingReqDTO reqDTO = new BriefingReqDTO();
				// reqDTO.setQtId(getManagedBean().getQt().getQtId());
				reqDTO.setScQt(qt);
				reqDTO.setLanguageCode(languageCode);
				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingSiteVisitDtl(reqDTO);
				// getting Briefing Same As Site Visit value
				ScQt scQt = resDTO.getScQt();
				getManagedBean().setQt(scQt);
				Long currentStatus = this.getManagedBean().getQt()
						.getQtStatus().getStatusId();
				this.getManagedBean().setCurrentStatusId(currentStatus);
				if (scQt.isZonal()) {
					List<TplSpecZoneDtl> zones = resDTO.getZonesList();
					List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
					if (null != zones) {
						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
						briefingReqDTO.setQtId(getManagedBean().getQt()
								.getQtId());
						briefingReqDTO.setScQt(getManagedBean().getQt());
						for (TplSpecZoneDtl specZoneDtl : zones) {
							briefingReqDTO.setSpecZoneId(specZoneDtl
									.getSpecZoneId());
							BriefingResDTO briefingResDTO = qTBriefingSO
									.retrieveBriefingSiteVisitDtls(briefingReqDTO);
							ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
							if (null != scQtBsv.getScQtBsvDtls()) {
								for (ScQtBsvDtl bsvDtl : scQtBsv
										.getScQtBsvDtls()) {
									if (QTConstants.BSV_TYPE_SITEVISIT
											.equalsIgnoreCase(bsvDtl
													.getBsvType())) {
										if (scQtBsv.getQtBsvId() == null) {
											scQtBsv.setAllSiteVisitReq(true);
										}
									}
								}
							}
							scQtBsv.setBsvRemarks(initializeRemarks(scQtBsv,
									this.getManagedBean().getQt()
											.getCurrentWorkFlowStatus(),
									scQtBsv.getBsvRemarks(),
									ScQtBSVRemark.class));
							BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
									specZoneDtl,
									briefingResDTO.getBriefingList(),
									briefingResDTO.getSiteVisitList(), scQtBsv);
							zoneBeans.add(briefingZoneBean);
						}
					}
					this.getManagedBean().setBriefingZoneBean(zoneBeans);

					this.getManagedBean().setBsvDtlBean(zoneBeans.get(0));
				} else {
					ScQtBsv qtBsv = resDTO.getScQtBsv();
					if (null != qtBsv.getScQtBsvDtls()) {
						for (ScQtBsvDtl bsvDtl : qtBsv.getScQtBsvDtls()) {
							if (QTConstants.BSV_TYPE_SITEVISIT
									.equalsIgnoreCase(bsvDtl.getBsvType())) {
								if (qtBsv.getQtBsvId() == null) {
									qtBsv.setAllSiteVisitReq(true);
								}
							}
						}
					}
					qtBsv.setBsvRemarks(initializeRemarks(qtBsv, this
							.getManagedBean().getQt()
							.getCurrentWorkFlowStatus(), qtBsv.getBsvRemarks(),
							ScQtBSVRemark.class));
					BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
							null, resDTO.getBriefingList(),
							resDTO.getSiteVisitList(), qtBsv);
					this.getManagedBean().setBsvDtlBean(briefingZoneBean);
				}
				QTBriefingSiteVisitBackingBean backingBean = new QTBriefingSiteVisitBackingBean();
				backingBean.retrievePmStateList();
			} finally {
				qTBriefingSO = null;
			}
		}
	}

	/**
	 * Process checked.
	 * 
	 * @param e
	 *            the e
	 */
	public void processChecked(AjaxBehaviorEvent e) {
		// Boolean id = (Boolean) e.getComponent().getAttributes().get("value");

	}

	/**
	 * Retrieve committee appointment tab dtl.
	 * 
	 * @param isApprovalReq
	 *            the is approval req
	 */
	public void retrieveCommitteeAppointmentTabDtl(boolean isApprovalReq, int reformCommitteeFlag) {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveCommitteeAppointmentTabDtl");
		
		if (null == this.getManagedBean().getQt().getFinalizationCommitte()) {
			this.getManagedBean().getQt()
					.setFinalizationCommitte(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getFinalizationSecratery()) {
			this.getManagedBean().getQt()
					.setFinalizationSecratery(new PmUser());
		}
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		Long qtId = scQt.getQtId();
		if (null == committeTabs) {
			String evaluationType = this.getManagedBean().getQt()
					.getEvaluationType();
			String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
			ParameterHelper parameterHelper = ParameterHelper.getInstance();

			committeTabs = new ArrayList<TabBean>();

			String tabType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					qtId);
			ScCommittee committee = specCommitte.getScCommittee();
			if ((reformCommitteeFlag & REFORM_SC) == REFORM_SC) {
				committee.setModified(true);
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
			}

			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, specCommitte));

			if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())) {
				boolean reformCommittee;
				// evaluation type is one tier only oc is added..
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationType)) {
					tabType = ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
					ScQtCommittee evalCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						committee = evalCommitte.getScCommittee();
						committee.setApprovalReq(isApprovalReq);
						reformCommittee = (reformCommitteeFlag & REFORM_OC) == REFORM_OC ||
								(reformCommitteeFlag & REFORM_TEC) == REFORM_TEC ||
								(reformCommitteeFlag & REFORM_FEC) == REFORM_FEC;
						
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							evalCommitte));
				}
				// evaluation type is two tier below committe are added
				else {
					tabType = ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE;
					ScQtCommittee openingCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						committee = openingCommitte.getScCommittee();
						committee.setApprovalReq(isApprovalReq);
						reformCommittee = (reformCommitteeFlag & REFORM_OC) == REFORM_OC;
								
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							openingCommitte));

					tabType = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
					ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						committee = tecCommitte.getScCommittee();
						committee.setApprovalReq(isApprovalReq);
						reformCommittee = (reformCommitteeFlag & REFORM_TEC) == REFORM_TEC;
						
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							tecCommitte));

					tabType = ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE;
					ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						committee = fecCommitte.getScCommittee();
						committee.setApprovalReq(isApprovalReq);
						reformCommittee = (reformCommitteeFlag & REFORM_FEC) == REFORM_FEC;
						
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							fecCommitte));
				}
				// FC tab
				PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
				tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
				StringBuilder fcTitleLabel = new StringBuilder(
						"lbl.sc.finalization.committee.secretary.");
				String fcTitleName = ResourceBundleUtils
						.getMessage(fcTitleLabel.toString()
									+ SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE.toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
				if (fcSecretaryCode == null
						|| fcSecretaryCode.getParameterId() == null) {
					SelectItem[] fcSecretaries = retrieveFinalizationCommitteeType();
					if (fcSecretaries != null && fcSecretaries.length > 0) {
						fcSecretaryCode = retrieveParameterById(Long
								.valueOf(fcSecretaries[0].getValue().toString()));
						;
					} else {
						fcSecretaryCode = null;
					}
				}

				// Displaying the Secretary Details
				if (fcSecretaryCode != null) {
					
					String fcSecretary = fcSecretaryCode.getParameterCode();

					if (fcSecretary != null) {
						if (fcSecretary
								.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
							fcTitleLabel
									.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
						} else {
							fcTitleLabel
									.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
						}
						fcTitleName = ResourceBundleUtils.getMessage(
								fcTitleLabel.toString().toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
					}
				}

				committeTabs.add(new TabBean(fcTitleName, tabType, null));
			}
		} 

		this.getManagedBean().setScCommitteeMemberTabList(committeTabs);

		for (TabBean tabBean : committeTabs) {
			if ((!SourcingConstants.COMMITTE_TYPE_FINALIZATION.equals(tabBean
					.getTabType()) && tabBean.getScQtCommittee()
					.getScCommittee().getCommitteStatus().getStatusId()
					.equals(QTStatusConst.CMT_COMPLETED))
					|| (SourcingConstants.COMMITTE_TYPE_FINALIZATION
							.equals(tabBean.getTabType())
							&& scQt.getFinalizationCommitte() != null
							&& scQt.getFinalizationCommitte().getParameterId() != null
							&& scQt.getFinalizationSecratery() != null && scQt
							.getFinalizationSecratery().getUserId() != null)) {
				tabBean.setIcon("ui-icon-endorse");
			}
		}
		if (committeTabs.size() > 0) {
			// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
			this.getManagedBean().setSelectedTabBean(
					committeTabs.get(this.getManagedBean()
							.getSelectedCommitteeTabIndex()));
		}

	}

	/**
	 * Retrieve finalization committee types.
	 * 
	 * @return the list
	 */
	public SelectItem[] retrieveFinalizationCommitteeType() {
		
		ScQt scQt = getManagedBean().getQt();
		SelectItem[] finalizationSecratary = getManagedBean().getFinalizationSecratary();
		Long currentStatusId = scQt.getQtStatus().getStatusId();
		
		if (finalizationSecratary == null
				&& isQTStatusLessThanOrEquals(currentStatusId, QTStatusConst.QT_PENDING_LOA)) {
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

				CommitteReqDTO reqDTO = new CommitteReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				CommitteResDTO resDTO = committeSO
						.retrieveFinalizationCommitteeType(reqDTO);

				List<PmParameter> finalizationCommTypes = resDTO.getFinalizationSecratry();
				if(CollectionUtils.isNotEmpty(finalizationCommTypes)){
					finalizationSecratary = new SelectItem[finalizationCommTypes.size()];
					for(int i=0;i<finalizationCommTypes.size();i++){
						String commTypeDesc = getParameterDesc(
								finalizationCommTypes.get(i),new FacesScopes().getUserLocale());
						finalizationSecratary[i] = new SelectItem(
								finalizationCommTypes.get(i).getParameterId(),commTypeDesc);
					}
					getManagedBean().setFinalizationSecratary(finalizationSecratary);
				}

				
			} finally {
				committeSO = null;
			}
		}

		return finalizationSecratary;
	}

	/**
	 * Retrieve business owner tab dtl.
	 */
	public void retrieveBusinessOwnerTabDtl() {
		debugLogger("retrieveBusinessOwnerTabDtl");
		if (getManagedBean().getQt().getQtId() != null) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO
						.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
				List<ScCommitteeMember> committeeMembersList = resDTO
						.getCommMemberResultBean().getResultList();
				getManagedBean().setScCommitteeMemberList(committeeMembersList);
			} finally {
				committeSO = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#
	 * preRenderView()
	 */
	public void preRenderView() {
		try {
			renewSesionObject();
			debugLogger("preRenderView, @Managedbean = "+ this.getManagedBean());
			
			Long qtId = null;
			Object payLoad = getBpmTaskDetails(Object.class, null);
			String taskName = this.getManagedBean().getTaskName();
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_FC_SECRETARY
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_SCORING_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				
				boolean isCommitteeAppointmentRemark = !SCProcessNameConst.ASSIGN_FC_SECRETARY.equalsIgnoreCase(taskName);
				Long currentStatusId = this.getManagedBean().getCurrentStatusId();
				if (!SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
						.equalsIgnoreCase(taskName)
						&& !SCProcessNameConst.ASSIGN_FC_SECRETARY
								.equalsIgnoreCase(taskName)
						&& !SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
								.equalsIgnoreCase(taskName)) {

					SCCommitteeDataType committeDataType = (SCCommitteeDataType) payLoad;

					qtId = committeDataType.getQtId().longValue();
				} else {

					SCEvaluationDataType evalDataType = (SCEvaluationDataType) payLoad;
					qtId = evalDataType.getQtId().longValue();

				}
				
				retrieveQuationTenderDetails(new Long(qtId));
				if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
						.equalsIgnoreCase(taskName)) {
					retrieveCommitteeAppointmentTabDtl(true, REFORM_NO_ACTION);
				} else if (SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
								.equalsIgnoreCase(taskName)) {
					IQTSuppFinalizationSO qtSuppFinalizationSO = null;
					try {
						qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

						SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
						reqDTO.setQtId(qtId);
						ScQtFinalization scQtFinalization = qtSuppFinalizationSO.retrieveSupplierFinalizationByQtId(reqDTO)
							.getScQtFinalization();
						int reformCommitteeFlag = REFORM_NO_ACTION;
						if (scQtFinalization.isReEvaluateCompReq()) {
							reformCommitteeFlag += REFORM_OC;
						}
						if (scQtFinalization.isReEvaluateTechReq()) {
							reformCommitteeFlag += REFORM_TEC;
						}
						if (scQtFinalization.isReEvaluateFinReq()) {
							reformCommitteeFlag += REFORM_FEC;
						}
						retrieveCommitteeAppointmentTabDtl(true, 
								reformCommitteeFlag);
						removeCommitteeActivityDetails(this.getManagedBean().getQt());

					} finally {
						qtSuppFinalizationSO = null;
					}
					
				} else if (SCProcessNameConst.ASSIGN_SCORING_COMMITTEE
						.equalsIgnoreCase(taskName)) {
					retrieveCommitteeAppointmentTabDtl(true, REFORM_SC);
					
				} else {
					retrieveCommitteeAppointmentTabDtl(false, REFORM_NO_ACTION);
				}
				
				if (isCommitteeAppointmentRemark) {
					initializeRemarksForCommitteeApproval();
				}
				
				this.getManagedBean().setReadOnly(false);
				this.getManagedBean().setCommitteeAppointment(true);
				this.getManagedBean().setCommitteeAppointmentRemark(isCommitteeAppointmentRemark);
				this.getManagedBean().setActiveIndex(2);
				this.getManagedBean().setCurrentStatusId(currentStatusId);
			} else {
				// qtId = quotationTenderDataType.getQtId().longValue();
				SCQuotationTenderDataType quationDataType = (SCQuotationTenderDataType) payLoad;
				qtId = quationDataType.getQtId().longValue();
				// retrieveFinalizationSecratryDtl(qtId);
				retrieveQuationTenderDetails(new Long(qtId));
			}

			if (null == this.getManagedBean().getQt().getProcurementMode()) {
				this.getManagedBean().getQt().setProcurementMode(new PmParameter());
			}

			if (SCProcessNameConst.REVISE_PCB.equalsIgnoreCase(this
					.getManagedBean().getTaskName())) {
				this.getManagedBean().setPcbProcess(true);
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);
			
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	private void initializeRemarksForCommitteeApproval() {
		ScQt scQt = this.getManagedBean().getQt();
		List<ScQtApproval> approvals = scQt.getScQtApprovals();

		for (ScQtApproval scQtApproval : approvals) {
			if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
					.equalsIgnoreCase(scQtApproval
							.getApprovalCategory().getParameterCode())) {
				IScWorkFlowStatusSO workFlowStatusSO;
				try {
					workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
					ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
					reqDTO.setDocType(ParameterConst.DOCTYPE_COMMITTE_APROVAL);
					reqDTO.setDocId(scQt.getQtId());
					
					ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					initializeRemarks(scQtApproval, currentWorkFlowStatus,
							scQtApproval.getScQtApprovalRemarks(),
							ScQtApprovalRemark.class);
					this.getManagedBean().setScQtApproval(scQtApproval);
				} finally {
					workFlowStatusSO = null;
				}

				break;
			}
		}
	}

	/**
	 * Update bpm task.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	public String updateBpmTask(String outcome) {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("updateBpmTask");

		updateApprovalDetails(scQt);
		retrieveApprovalTabDtl();

		// Code will be called if user didn't visit the supplier qualification
		// tab.
		if (null == this.getManagedBean().getSupplierCriteria()) {
			retrieveSuppQualificationTabDtl();
		}

		QuationTenderReqDTO qtReqDTO = prepareQTInfo();

		if (this.getManagedBean().isPcbProcess()) {
			qtReqDTO.setPcbProcess(true);
		}

		FacesScopes facesScopes = new FacesScopes();
		Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		String taskName = this.getManagedBean().getTaskName();

		qtReqDTO.setOutcome(outcome);
		IQTDetailSO detailSO;
		try {
			ScQt qt = qtReqDTO.getQtdetail();
			Object payLoad = getManagedBean().getPayLoad();
			// Fix for Other Approval Validations on submit only
			qt.setOtherApproval(true);
			qt.setFCApproval(true);
			if (ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA
					.equalsIgnoreCase(qt.getPmProcurementCategory()
							.getParameterCode())) {
				qt.setMitiApproval(true);
				// qt.setMitiApproval(true);
			}

			Long currentStatus = qt.getQtStatus().getStatusId();
			if (currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					|| currentStatus.equals(QTStatusConst.QT_REVISION_PUBLICATION)
					|| currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION)) {

				// check if its Pending Schedule and Documents , only execute
				// this part
				if (null == this.getManagedBean().getScQtDocReportList()) {
					retrieveDocumentTabDtl();

					qt.setScQtDocReports(this.getManagedBean()
							.getScQtDocReportList());
				}
				if (this.getManagedBean().getQt().isSampleReq()) {
					this.getManagedBean().setSubmit(true);
					retrieveScheduleTabDtl();
					qt.setScQtSampleSubmission(this.getManagedBean()
							.getScQtSampleSubmissionList());
					qt.setSampleClosingDate(qt.getClosingDate());
				} else {
					retrieveScheduleTabDtl();
					
				}
				
				qt.setClosingDate(getManagedBean().getQt().getClosingDate());
				if (null == qt.getProposalStartDate()) {
					qt.setProposalStartDate(getManagedBean().getQt()
							.getProposalStartDate());
				}
				/*
				 * QTScheduleDetailBackingBean scheduleBackingBean = new
				 * QTScheduleDetailBackingBean();
				 * scheduleBackingBean.handlePublishClosingDate();
				 * qt.setClosingDate(getManagedBean().getQt().getClosingDate());
				 */
			}
			CommitteResDTO committeResDto = null;
			QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_SCORING_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				updateApprovalRequestDtls(qt.getScQtApprovals(),
						ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(payLoad);
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
				committeReqDTO.setScQt(qt);
				committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				committeReqDTO.setToBeDeletedMembers(this.getManagedBean()
						.getToBeDelCommitteMemebers());
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				
			} else if (SCProcessNameConst.ASSIGN_FC_SECRETARY
					.equalsIgnoreCase(taskName)) {
				validateFCSecretaryDtl(qt.getFinalizationCommitte(), qt.getFinalizationSecratery());
				
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(payLoad);
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO
						.updateFinalizateSecrataryTask(committeReqDTO);

			} else {
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setPayLoad(payLoad);
				qtReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				FacesScopes facesScope = new FacesScopes();
				qtReqDTO.setPmUser(facesScope.getUserSessionBean());
				qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
				qtReqDTO.setRoleCode(getUserRoleCode());
				qtReqDTO.setUserLocale(facesScope.getUserLocale());
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				String approvalCategory = ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT;
				if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
					approvalCategory = ParameterConst.APPROVAL_CATEGORY_BPK;
				} else if (SCProcessNameConst.REVISE_PCB
						.equalsIgnoreCase(taskName)) {
					approvalCategory = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
				} else if (SCProcessNameConst.REVISE_QT_SCHEDULE
						.equalsIgnoreCase(taskName)) {
					approvalCategory = ParameterConst.APPROVAL_CATEGORY_PTJ;
				}
				if (null != approvalCategory) {
					updateApprovalRequestDtls(qt.getScQtApprovals(),
							approvalCategory);
				}
				qtResDTO = (QuationTenderResDTO) detailSO
						.updateBPMAction(qtReqDTO);
			}
			if ((null != committeResDto && committeResDto.isSuccessful())
					|| (null != qtResDTO && qtResDTO.isSuccessful())) {

				renewSesionObject();
				
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMsgCodeByTaskName(taskName),
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				debugLogger("end of updateBpmTask");
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	/**
	 * Sets the out come action.
	 * 
	 * @param outCome
	 *            the new out come action
	 */
	public void setOutComeAction(String outCome) {
		this.getManagedBean().setOutCome(outCome);

	}

	/**
	 * Retrieve approval tab dtl.
	 */
	public void retrieveApprovalTabDtl() {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveApprovalTabDtl");
		// Added by Fara on 03092014 - Fix for Approval tab if user dint visit
		// the Committee Appointment tab
		List<TabBean> tabList = this.getManagedBean()
				.getScCommitteeMemberTabList();
		if (CollectionUtils.isEmpty(tabList)) {
			retrieveCommitteeAppointmentTabDtl(false, REFORM_NO_ACTION);
		}
		updateQtDtls();
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO resDTO = null;

			reqDTO.setQtdetail(scQt);
			reqDTO.setAvaliableApprovals(scQt.getScQtApprovals());
			reqDTO.setAmendMentBean(this.getManagedBean().getAmendMentBean());
			reqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
			// reqDTO.setRuleDataBean(this.getManagedBean().getRuleDataBean());
			resDTO = detailSO.retriveQTApprovalDetails(reqDTO);

			List<ScQtApproval> requiredApproval = resDTO.getApproval();

			List<TabBean> tabBeans = new ArrayList<TabBean>();
			List<TabBean> existingApprovalTabs = this.getManagedBean()
					.getTabList();

			ParameterHelper parameterHelper = ParameterHelper.getInstance();
			if (requiredApproval != null) {
				for (ScQtApproval qtApproval : requiredApproval) {
					qtApproval = checkIsApprovalExists(qtApproval,
							existingApprovalTabs);

					if (!qtApproval.isDeleted()) {
						String approvalCategoryCode = qtApproval
								.getApprovalCategory().getParameterCode();
						String titleName = parameterHelper.getParameterDesc(
								ParameterTypeConst.APPROVAL_CATEGORY,
								approvalCategoryCode);

						qtApproval.setScQtApprovalRemarks(initializeRemarks(
								qtApproval, scQt.getCurrentWorkFlowStatus(),
								qtApproval.getScQtApprovalRemarks(),
								ScQtApprovalRemark.class));
						// qtApproval.setScQtApprovalAttachment(null);
						// qtApproval.setScQt(this.getManagedBean().getQt());

						/*
						 * // BUDGET_APPROVAL if
						 * (qtApproval.getApprovalCategory().getParameterCode()
						 * .equals(ParameterConst.APPROVAL_CATEGORY_BUDGET)) {
						 * tabBeans.add(new
						 * TabBean(qtApproval.getApprovalType(), qtApproval,
						 * approverSettingDtls( RoleCode.BUDGET_APPROVER,
						 * qtApproval))); } else
						 */
						List<PmParameter> settings = null;
						if (ParameterConst.APPROVAL_CATEGORY_BPK
								.equals(approvalCategoryCode)) {
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval, settings));
						} else if (ParameterConst.APPROVAL_CATEGORY_PTJ
								.equals(approvalCategoryCode)) {
							Long statusId = scQt.getQtStatus().getStatusId();
							if (isQTStatusPublished(statusId)) {
								tabBeans.add(new TabBean(titleName,
										approvalCategoryCode, qtApproval,
										approverSettingDtls(
												RoleCode.PUBLICATION_APPROVER,
												scQt.getPmOrgProfile()
														.getOrgProfileId(),
												qtApproval)));
							} else if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) ||
									QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId) ||
									isStatusOfBpkOrScheduleRevise(statusId)) {
								this.getManagedBean().setPublicationApproval(qtApproval);
							}
						} else if (ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE
								.equals(approvalCategoryCode) ||
								ParameterConst.APPROVAL_CATEGORY_PROCUREMENT_BOARD
								.equals(approvalCategoryCode)) {
							List<ScQtApproverSettings> approvalSettings = qtApproval
									.getApproverSettings();
							if (null == approvalSettings
									|| approvalSettings.isEmpty()) {
								approvalSettings = new ArrayList<ScQtApproverSettings>();
								ScQtApproverSettings approverSettings = new ScQtApproverSettings();
								approverSettings.setPmUser(new PmUser());
								approverSettings.setScQtApproval(qtApproval);
								approverSettings.setCommitteeType(new PmParameter());
								approvalSettings.add(approverSettings);
								qtApproval
										.setApproverSettings(approvalSettings);
							} else {
								for (ScQtApproverSettings approverSettings : approvalSettings) {
									approverSettings.setScQtApproval(qtApproval);
									
									// Added to populate security if Finalization committee is same as Committee Appointment
									PmParameter finalizationCommitee = scQt.getFinalizationCommitte();
									PmParameter committeeSettings = approverSettings.getCommitteeType();
									if (committeeSettings != null
											&& finalizationCommitee != null
											&& finalizationCommitee.getParameterId() != null
											&& finalizationCommitee.getParameterCode()
													.equalsIgnoreCase(committeeSettings.getParameterCode())) {
										approverSettings.setPmUser(scQt.getFinalizationSecratery());
									}
								}
							}
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval,
									retrieveFinalizationCommitteeType()));
						} else if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
								.equals(approvalCategoryCode)) {
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval,
									approverSettingDtls(
											RoleCode.COMMITTEE_APPROVER, scQt
													.getPmOrgProfile()
													.getOrgProfileId(),
											qtApproval)));
						} else if (ParameterConst.APPROVAL_CATEGORY_OTHERS
								.equals(approvalCategoryCode)) {
							List<ScQtApprovalRequest> approvalRequest = qtApproval
									.getScQtApprovalDtls();
							if (null != approvalRequest
									&& !approvalRequest.isEmpty()) {
								for (int i = 0; i < approvalRequest.size(); i++) {
									PmParameter parameter = approvalRequest
											.get(i).getApproverAction();
									if (null == parameter) {
										approvalRequest.get(i)
												.setApproverAction(
														new PmParameter());
										ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
										approvalRemark
												.setApprovalRequest(approvalRequest
														.get(i));
										approvalRequest.get(i)
												.setAppRequestRemark(
														approvalRemark);
									} else {
										if (null == approvalRequest.get(i)
												.getAppRequestRemark()) {
											ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
											approvalRemark
													.setApprovalRequest(approvalRequest
															.get(i));
											approvalRequest.get(i)
													.setAppRequestRemark(
															approvalRemark);
										}
									}
								}
							}
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval, settings));
						}
					} else {
						if (this.getManagedBean().getToBeRemoveList() == null) {
							this.getManagedBean().setToBeApprovalRemoveList(
									new ArrayList<ScQtApproval>());

						}
						this.getManagedBean().getToBeApprovalRemoveList()
								.add(qtApproval);
					}
				}
			}
			
			for (TabBean approvalTabBean : tabBeans) {
				if (ParameterConst.APPROVAL_CATEGORY_OTHERS
						.equals(approvalTabBean.getTabType())) {
					approvalTabBean.setTooltip(ResourceBundleUtils.getMessage(
							"lbl.sc.qt.approval."
									+ approvalTabBean.getTabType()
											.toLowerCase() + ".tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
				}
			}

			// Added by Fara on 03092014 - Fix for Approval tab if no data for
			// approval
			if (CollectionUtils.isNotEmpty(tabBeans)) {
				int selectedTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
				if (selectedTabIndex > tabBeans.size()-1) {
					selectedTabIndex = 0;
				}
				this.getManagedBean().setSelectedApprovalTabBean(
						tabBeans.get(selectedTabIndex));
				this.getManagedBean().setScQtApproval(
						this.getManagedBean().getSelectedApprovalTabBean()
								.getScQtApproval());
			}
			this.getManagedBean().setTabList(tabBeans);
		} finally {
			detailSO = null;
		}
	}

	/**
	 * Retrieve supp qualification tab dtl.
	 */
	public void retrieveSuppQualificationTabDtl() {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveSuppQualificationTabDtl");
		
		if (!scQt.isBrandNew() && scQt.isZonal() && scQt.isPanel()) {
			retrieveQuationTenderDetails(scQt.getQtId());
		}
		IQTDetailSO QtDetailsSO;
		QuationTenderResDTO tenderResDTO;

		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			// tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());

			tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);

			getManagedBean().setSupplierCriteria(tenderResDTO.getQtDetail().getScQtSupplierCriteria());
			// Commented as a fix for SIR#9289
//			scQt = tenderResDTO.getQtDetail();
			this.getManagedBean().setAllStateCode(tenderResDTO.getStateCode());
			if (!this.getManagedBean().getQt().isPanel()) {
				if (CollectionUtils.isEmpty(this.getManagedBean()
						.getQtCategoryList())) {
					List<ScQtCategory> categoryResults = tenderResDTO
							.getCategories();

					this.getManagedBean().setQtCategoryList(categoryResults);

				}

				if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
						.equalsIgnoreCase(scQt.getQtType())) {
					if (CollectionUtils.isEmpty(this.getManagedBean()
							.getQtSupplierList())) {
						this.getManagedBean().setQtSupplierList(
								tenderResDTO.getRestrictedSuppliers());
						this.getManagedBean()
								.setTotalSuppliers(
										tenderResDTO.getRestrictedSuppliers() != null ? tenderResDTO
												.getRestrictedSuppliers()
												.size() : 0);
						if (ParameterConst.OPEN_FOR_BUMI.equals(scQt.getOpenFor())) {
							CreateQtBackingBean backingBean = new CreateQtBackingBean();
							backingBean.clearNonBumiSuppliers(scQt);
						}
					}
				} else {
					if (tenderResDTO.getTotalSuppliers() != null) {
						this.getManagedBean().setTotalSuppliers(tenderResDTO.getTotalSuppliers());	
					}
				}

				boolean isQuotationThresHold = ParameterConst.PROCUREMENT_MODE_QUOTATION
						.equals(scQt.getProcurementMode().getParameterCode());
				if (!isQuotationThresHold
						&& !scQt.getScQtSupplierCriteria().isAllState()) {
					if(this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_PENINSULAR){
						scQt.getScQtSupplierCriteria().setAllState(true);
					} else if(null == scQt.getScQtSupplierCriteria()
							.getLocationLevel()){
						scQt.getScQtSupplierCriteria().setLocationLevel(new PmParameter());
					}
					
				}

				if (isQuotationThresHold
						&& ParameterConst.QUOTATION_TENDER_TYPE_OPEN
								.equalsIgnoreCase(scQt.getQtType())) {
					if (null == scQt.getScQtSupplierCriteria()
							.getLocationLevel()) {
						scQt.getScQtSupplierCriteria().setLocationLevel(
								new PmParameter());
					}
					scQt.getScQtSupplierCriteria().setAllState(false);
				}

				if (!scQt.getScQtSupplierCriteria().isAllState()) {
					List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
					
					if (null != tenderResDTO.getLocationDetails() && !tenderResDTO.getLocationDetails().isEmpty()) {
						if (null == this.getManagedBean()
								.getTotalSuppliersSpecLoc()) {
							this.getManagedBean().setTotalSuppliersSpecLoc(
									new ArrayList<TplSpecLoc>());
						}
						getManagedBean().setZoneTrees(null);
						this.getManagedBean().getTotalSuppliersSpecLoc()
								.addAll(tenderResDTO.getLocationDetails());
						for (TplSpecLoc localLoc : tenderResDTO
								.getLocationDetails()) {
							if (null != scQt.getScQtSupplierCriteria()
									&& null != scQt.getScQtSupplierCriteria()
											.getLocationLevel()) {
								this.getManagedBean().setLocalLevelCode(
										scQt.getScQtSupplierCriteria()
												.getLocationLevel()
												.getParameterCode());
							}
							getManagedBean().setZoneTrees(
									new ArrayList<TreeNode>());
							ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
							locCriteria.add(criteria);
						}
						if (null != getManagedBean().getZoneTrees()
								&& getManagedBean().getZoneTrees().size() == 0) {
							this.getManagedBean()
									.setExistingSupplierLocationList(
											locCriteria);
							getManagedBean().getZoneTrees().addAll(
									LocationSingleton.getInstance()
											.constuctTreeNode(locCriteria));
							scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
						}
					} else {
						if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())
								&& !scQt.getScQtSupplierCriteria().isAllState()
								&& ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(scQt.getQtType())) {
							if (null == this.getManagedBean().getZoneTrees()
									|| this.getManagedBean().getZoneTrees().isEmpty()) {
								retrieveNonZonalDtls();
							}
						}
					}
				}
			} else {
				this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
				if(CollectionUtils.isNotEmpty(tenderResDTO.getPanelItems())){
					this.getManagedBean().setPanelItemList(
							tenderResDTO.getPanelItems());
				}
				
				if (null != this.getManagedBean().getQt().getCircularVer()) {
					this.getManagedBean().setCircularExpDate(
							this.getManagedBean().getQt().getCircularVer()
									.getEndDate());
					this.getManagedBean().setCircularNo(
							this.getManagedBean().getQt().getCircularVer()
									.getCircularNo());
					this.getManagedBean().setCircularName(
							this.getManagedBean().getQt().getCircularVer()
									.getName());
					this.getManagedBean().setCircularType(
							this.getManagedBean().getQt().getCircularVer()
									.getDocType());
				}
				if (this.getManagedBean().getZoneGeneralTrees()== null ||
						this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					retriveZonesByCircularNo();
				}
			}
		} finally {

		}
	}
	

	/**
	 * Retrive zones by circular no.
	 */
	private void retriveZonesByCircularNo() {
		debugLogger("retriveZonesByCircularNo");
		IPanelSupplierSO panelSupplierSO;
		
		PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {
			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);

			List<CtPanelItem> panelItemList = getManagedBean().getPanelItemList();
			
			if (CollectionUtils.isNotEmpty(panelItemList)){

				reqDTO.setPanelItem(panelItemList.get(0));
				resDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqDTO);
			}

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {
				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {
					this.getManagedBean().setCtzones(true);
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					if (null != getManagedBean().getZoneGeneralTrees()
							&& getManagedBean().getZoneGeneralTrees().size() == 0) {

						for (CtPanelZone panelZones : resDTO.getCtPanelZones()) {

							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(panelZones.getName());
							tplSpecZoneDtl.setPanelZone(panelZones);
							setUserDetails(tplSpecZoneDtl);

							List<PmState> states = SourcingLocationHelper
									.getStateFromCtPanelZone(panelZones
											.getPanelZoneDtls());
							List<PmDistrict> districts = SourcingLocationHelper
									.getDistrictFromCtPanelZone(panelZones
											.getPanelZoneDtls());

							getManagedBean()
									.getZoneGeneralTrees()
									.add(LocationSingleton.getInstance()
											.buildTreeWithDisabledChilds(
													tplSpecZoneDtl, states,
													districts, null, null,
													true, TplSpecZoneLoc.class));
						}
					}
				}
			} else {
				
				this.getManagedBean().setTotalSuppliers(0);
				
				if (CollectionUtils.isNotEmpty(panelItemList)) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSupplierSO.class);
					QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
					QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
					supplierReqDTO.setPanelItemId(panelItemList.get(0).getPanelItemId());
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
					
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != supplierResDTO.getCtPanelSuppliers()) {
						this.getManagedBean().setTotalSuppliers(
								supplierResDTO.getCtPanelSuppliers().size());
						this.getManagedBean().setCtZoneSupplierList(
								supplierResDTO.getCtPanelSuppliers());
					}
				}
			}
		} finally {
		}
	}

	/**
	 * Retrieve schedule tab dtl.
	 */
	public void retrieveScheduleTabDtl() {
		debugLogger("retrieveScheduleTabDtl");

		BriefingZoneBean briefingList = this.getManagedBean().getBsvDtlBean();
		if (briefingList == null) {
			retrieveBriefingSitevisitTabDtl();
		}

		if (this.getManagedBean().getScQtContactPersons() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				// reqDTO.setQtId(getManagedBean().getQt().getQtId());
				reqDTO.setQtdetail(this.getManagedBean().getQt());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTScheduleDetails(reqDTO);
				ScQt scQt = resDTO.getQtDetail();
				this.getManagedBean().setQt(scQt);
				// Set New publish date for update publish date. To solve issue on publish date reset back to original time during primefaces calender update phase.
				scQt.setNewPublishDate(scQt.getPublishDate());
				// Set New closing date for update closing date.
				scQt.setNewClosingDate(scQt.getClosingDate());
				ISearchResultBean<ScQtContactPerson> contactPersons = resDTO
						.getContactPersons();
				List<ScQtContactPerson> qtContactPerson = contactPersons
						.getResultList();
				if (qtContactPerson == null || qtContactPerson.isEmpty()) {
					ScQtContactPerson contactPerson = new ScQtContactPerson();
					PmUser user = new FacesScopes().getUserSessionBean();
					contactPerson.setPhoneNo(user.getPhoneNo());
					contactPerson.setUser(user);
					contactPerson.setEmail(user.getEmail());
					contactPerson.setFaxNo(user.getFaxNo());
					contactPerson
							.setOfficerType(SourcingConstants.OFFICER_TYPE_QT);
					contactPerson.setName(user.getUserName());
					contactPerson.setScQt(this.getManagedBean().getQt());
					qtContactPerson.add(contactPerson);
				}
				this.getManagedBean().setScQtContactPersons(qtContactPerson);
				this.getManagedBean().setPmuserContactpersonList(
						resDTO.getDeskOfficers().getResultList());
				QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
				if (this.getManagedBean().getQt().isSampleReq()
						&& (null == this.getManagedBean()
								.getScQtSampleSubmission() && isEmptyOrNull(this
								.getManagedBean().getScQtSampleSubmissionList()))) {
					if (this.getManagedBean().getQt().isZonal()) {
						this.getManagedBean().setScQtSampleSubmissionList(
								resDTO.getSampleSubmissionResultBean());
					} else {
						this.getManagedBean().setScQtSampleSubmission(
								resDTO.getSampleSubmissionResultBean().get(0));
						if (null != this.getManagedBean()
								.getScQtSampleSubmission()) {
							if (null == this.getManagedBean()
									.getScQtSampleSubmissionList()) {
								this.getManagedBean()
										.setScQtSampleSubmissionList(
												new ArrayList<ScQtSampleSubmission>());
							}
							this.getManagedBean()
									.getScQtSampleSubmissionList()
									.add(this.getManagedBean()
											.getScQtSampleSubmission());
						}
						if (!getManagedBean().isSubmit()) {
							backingBean.retrievePmStateList();
						}
					}
				}

				backingBean.getDeskOfficerAdminAssistant();
			} finally {
				detailSO = null;
			}
		}
		if (this.getManagedBean().getQt().isSampleReq()) {
			QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
			if (!getManagedBean().isSubmit()) {
				backingBean.retrievePmStateList();
			}
		}
	}

	/**
	 * Retrieve deduction payment tab dtl.
	 */
	public void retrieveDeductionPaymentTabDtl() {
		
		ScQt scQt = getManagedBean().getQt();
		debugLogger("retrieveDeductionPaymentTabDtl");
		if (this.getManagedBean().getScQtDeductionList() == null || this.getManagedBean().getScQtDeductionList().isEmpty()) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);
				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();
				this.getManagedBean().setScQtDeductionList(deductionList);
				getManagedBean().setSelectedScQtDeduction(null);
				/*
				 * if (null != deductionList && !deductionList.isEmpty()) {
				 * this.
				 * getManagedBean().setSelectedScQtDeduction(deductionList.get
				 * (0)); this.getManagedBean().getSelectedScQtDeduction().
				 * setScQtDeductionClauses
				 * (deductionList.get(0).getScQtDeductionClauses()); }
				 */
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * Retrieve document tab dtl.
	 */
	public void retrieveDocumentTabDtl() {
		debugLogger("retrieveDocumentTabDtl");
		IQTDocumentSO qtDocumentSO;
		if (getManagedBean().getScQtDocReportList() == null) {
			QTDocumentResDTO resDTO = null;
			try {
				qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
				QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
				reqDTO.setScQt(getManagedBean().getQt());
				reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				resDTO = qtDocumentSO.retriveQTDocDetails(reqDTO);
				if (resDTO.getQtDocReports() != null
						&& !resDTO.getQtDocReports().isEmpty()) {
					this.getManagedBean().setScQtDocReportList(
							resDTO.getQtDocReports());
				}
			} finally {
				qtDocumentSO = null;
			}
		}
	}

	/**
	 * Retrieve activities tab dtl.
	 */
	public void retrieveActivitiesTabDtl() {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveActivitiesTabDtl");
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			// reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setQtdetail(scQt);
			QuationTenderResDTO resDTO = detailSO.retrieveQTActivities(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setQtActivityBeans(
						resDTO.getQtActivityBeans());
			}
		} finally {
			detailSO = null;
		}
	}

	/**
	 * Retrieve publication approval tab dtl.
	 */
	public void retrievePublicationApprovalTabDtl() {
		debugLogger("retrievePublicationApprovalTabDtl");
		IQTDetailSO detailSO;
		DualListModel<ScQtApproverSettings> dualListModel = null;
		try {
			ScQt scQt = getManagedBean().getQt();
			ScQtApproval publicationApproval = this.getManagedBean().getPublicationApproval();
			
			if (null == publicationApproval) {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_PTJ);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalDtlsByQtIdNType(reqDTO);
				publicationApproval = resDTO.getQtApproval();
				if (publicationApproval == null) {
					publicationApproval = new ScQtApproval();
					publicationApproval
							.setScQtApprovalRemarks(initializeRemarks(
									publicationApproval, scQt
											.getCurrentWorkFlowStatus(),
									publicationApproval
											.getScQtApprovalRemarks(),
									ScQtApprovalRemark.class));
					publicationApproval.setScQtApprovalAttachments(null);
					publicationApproval.setScQt(scQt);
					publicationApproval.setApprovalCategory(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_CATEGORY, 
							ParameterConst.APPROVAL_CATEGORY_PTJ));

				} 
				
				if (CollectionUtils.isEmpty(publicationApproval.getScQtApprovalDtls())) {
					List<ScQtApprovalRequest> approvalDtls = new ArrayList<ScQtApprovalRequest>();
					ScQtApprovalRequest scQtApprovalDtl = new ScQtApprovalRequest();
					scQtApprovalDtl.setApprovalType(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES, 
							ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL));
					scQtApprovalDtl.setScQtApproval(publicationApproval);
					approvalDtls.add(scQtApprovalDtl);
					publicationApproval.setScQtApprovalDtls(approvalDtls);	
				}
				publicationApproval.setDeleted(false);
				List<ScQtApproval> toBeApprovalRemoveList = this.getManagedBean().getToBeApprovalRemoveList();
				if (CollectionUtils.isNotEmpty(toBeApprovalRemoveList)) {
					toBeApprovalRemoveList.remove(publicationApproval);	
				}
				
				this.getManagedBean().setPublicationApproval(
						publicationApproval);
				log.info("Remark Size : "
						+ this.getManagedBean().getPublicationApproval()
								.getScQtApprovalRemarks().size());
			} 
			
			// set up approval pick list
			dualListModel = approverSettingDtls(
					RoleCode.PUBLICATION_APPROVER, scQt.getPmOrgProfile()
							.getOrgProfileId(), publicationApproval);
			this.getManagedBean().setPublicationApprovals(dualListModel);
			
			// set up remark.
			publicationApproval
			.setScQtApprovalRemarks(initializeRemarks(
					publicationApproval, scQt
							.getCurrentWorkFlowStatus(),
					publicationApproval
							.getScQtApprovalRemarks(),
					ScQtApprovalRemark.class));
		} finally {
			detailSO = null;
		}
	}

	/**
	 * The Class QTPagingDataModel.
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The qtn tender search bean. */
		private QtnTenderSearchBean qtnTenderSearchBean;

		/**
		 * Instantiates a new QT paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param qtnTenderSearchBean
		 *            the qtn tender search bean
		 */
		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId, false);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}

		/**
		 * retrieve list of ScQt by params.
		 * 
		 * @param searchBean
		 *            the search bean
		 * @return the i search result bean
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
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
				populateQTSearchCriteria(reqDTO, qtnTenderSearchBean, searchBean);
				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				
				// set search result doc type for navigate to diff page based on diff doc type selected.
				qtnTenderSearchBean.setResultDocType(qtnTenderSearchBean.getDocType());
				
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            the type
	 * @return the list
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.getManagedBean().getQtnTenderSearchBean().setDocType(ParameterConst.DOCTYPE_QUOTATION);
		this.getManagedBean().getQtnTenderSearchBean().setDocNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setProcurementMode(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtStatus(null);
		getDefaultPtjAndMinistryValues();
	}

	/**
	 * Update qt dtls.
	 */
	private void updateQtDtls() {
		List<TabBean> commiteTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommittes = null;
		if (null != commiteTabs) {
			qtCommittes = new ArrayList<ScQtCommittee>();
			for (TabBean tabBean : commiteTabs) {
				if (!SourcingConstants.COMMITTE_TYPE_FINALIZATION
						.equals(tabBean.getTabType())) {
					qtCommittes.add(tabBean.getScQtCommittee());
				}

			}
		}
		/*
		 * List<ScQtCommittee> qtCommittees = new ArrayList<ScQtCommittee>();
		 * qtCommittees.add(this.getManagedBean().getScQtCommittee());
		 */
		this.getManagedBean().getQt().setScQtCommittees(qtCommittes);
	}

	/**
	 * Search qt back action.
	 * 
	 * @return the string
	 */
	public String searchQtBackAction() {
		debugLogger("searchQtBackAction");
		return "/xhtml/sc/quatationtender/quatationtendersearch.xhtml?faces-redirect=true";
	}

	/**
	 * Retrieve quation tender details.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param docType
	 *            the doc type
	 */
	public void retrieveQuationTenderDetails(Long qtId) {
		
		debugLogger("retrieveQuationTenderDetails, @qtId"+qtId);
		if (null != qtId) {
			// this.getManagedBean().setActiveIndex(0);
			IQTDetailSO QtDetailsSO;
			try {
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setMaintanceFlow(this.getManagedBean()
						.isMaintanceFlow());
				tenderReqDTO.setQtId(qtId);
				tenderReqDTO.setBdgDtlsReq(true);
				QuationTenderResDTO tenderResDTO = null;
				ScQt scQt = this.getManagedBean().getQt();
				if (null == scQt
						|| (scQt.isZonal()
						/* && !scQt.isPanel() */
						&& null != this.getManagedBean().getZoneGeneralTrees() && this
								.getManagedBean().getZoneGeneralTrees()
								.isEmpty())) {
					tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
					if (tenderResDTO.getQtDetail() != null) {
						scQt = tenderResDTO.getQtDetail();
						getManagedBean().setQt(scQt);
						Long currentStatus = this.getManagedBean().getQt()
								.getQtStatus().getStatusId();
						this.getManagedBean().setCurrentStatusId(currentStatus);
						getManagedBean().getCtContract().setContractNo(
								this.getManagedBean().getQt()
										.getExistingContractNo());
						// this.getManagedBean().setContractType(scQt.getContractType());
						if (this.getManagedBean().getQt().isZonal()) {							
							if (null != this.getManagedBean().getQt()
									.getZoneLocationLevel()) {
								this.getManagedBean().setStateCodes(
										this.getManagedBean().getQt()
												.getZoneLocationLevel()
												.getParameterCode());
							}

							// Populate zone detail into tree node
							if (scQt.getScQtSupplierCriteria() != null &&
									scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
								List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
								populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
								// setting into zones bean to validate zones while adding 
								if (CollectionUtils.isNotEmpty(specZoneDtls)) {
									List<ZonesBean> zoneBeanLst = new ArrayList<ZonesBean>();
									for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
										ZonesBean bean = new ZonesBean();
										bean.setTplSpecZoneDtl(specZoneDtl);
										zoneBeanLst.add(bean);
									}
									this.getManagedBean().setBeans(zoneBeanLst);
								}
							}
						}

					}

					this.getManagedBean().setAmendMentBean(
							tenderResDTO.getAmendMentBean());
					if (this.getManagedBean().getGosodos() == null) {
						this.getManagedBean().setGosodos(
								tenderResDTO.getBudgetDtls());
					}
					if (null != getManagedBean().getQt()
							&& null == getManagedBean().getQt()
									.getProcurementTypeCatId()) {
						getManagedBean().getQt().setProcurementTypeCatId(
								new PmParameter());
					}

				} else {
					if (scQt.getEstimatedAmount() == null) {
						BigDecimal estimatedAmount = QtDetailsSO
								.getdecryptedEstAmount(qtId);
						scQt.setEstimatedAmount(estimatedAmount);
						scQt.setDecryptedEstAmt(estimatedAmount);
					}
					
					if (scQt.getTmpExpectedIntendedPrice() == null) {
						BigDecimal tmpExpectedIntendedPrice = QtDetailsSO
								.getDecryptedExpectedIntendedPrice(qtId);
						scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
						scQt.setDecExpectedIntendedPrice(tmpExpectedIntendedPrice);
					}
					// this.getManagedBean().setContractType(scQt.getContractType());
				}
			} finally {
				QtDetailsSO = null;
			}
			debugLogger("end of retrieveQuationTenderDetails");
		}
	}
	
	private void populateZoneDetailIntoTreeNode(ScQt scQt, List<TplSpecZoneDtl> specZoneDtls) {
		List<TreeNode> zoneGeneralTrees = getManagedBean().getZoneGeneralTrees();
		
		this.getManagedBean().setCtzones(Boolean.TRUE);
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		if (zoneGeneralTrees == null) {
			zoneGeneralTrees = new ArrayList<TreeNode>();
			getManagedBean().setZoneGeneralTrees(zoneGeneralTrees);
		} else {
			zoneGeneralTrees.clear();	
		}
		
		boolean zoneCoverageReadonly = enableFieldByStatusNFiledName(QTFieldConst.FILED_QT_ZONE_COVERAGE);
		List<TreeNode> zoneSuppQualTrees = null;
		if (zoneCoverageReadonly) {
			zoneSuppQualTrees = getManagedBean().getZoneSuppQualTrees();
			if (zoneSuppQualTrees == null) {
				zoneSuppQualTrees = new ArrayList<TreeNode>();
				getManagedBean().setZoneSuppQualTrees(zoneSuppQualTrees);
			} else {
				zoneSuppQualTrees.clear();	
			}	
		} else {
			getManagedBean().setZoneSuppQualTrees(null);
		}
		
		if (CollectionUtils.isNotEmpty(specZoneDtls)) {
			for (TplSpecZoneDtl zone : specZoneDtls) {
				if (CollectionUtils.isNotEmpty(zone.getTplSpecZoneLocs())) {
					List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
					
					for (TplSpecZoneLoc specLoc : zone.getTplSpecZoneLocs()) {
						if (specLoc.isZonalLocation()) {
							specZoneLocs.add(specLoc);
						}
					}
					zoneGeneralTrees.add(
							LocationSingleton.getInstance().constuctTreeNode(
									zone, specZoneLocs));
					
					if (zoneSuppQualTrees != null) {
						zoneSuppQualTrees.add(
								LocationSingleton.getInstance().constuctTreeNode(
										zone, zone.getTplSpecZoneLocs()));	
					}
				}
			}
		}
	}

	/**
	 * Gets the qt commite dtls by qt id n type.
	 * 
	 * @param commType
	 *            the comm type
	 * @param qtId
	 *            the qt id
	 * @return the qt commite dtls by qt id n type
	 */
	private ScQtCommittee getQtCommiteDtlsByQtIdNType(String commType, Long qtId) {
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		ParameterResDTO parameterResDTO = null;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(qtId);
		CommitteResDTO resDTO = null;
		ScQtCommittee qtCommitte = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
			if (null == qtCommitte) {
				if (ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);

				} else if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);

				}

				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
				qtCommitte = new ScQtCommittee();
				qtCommitte.setScQt(this.getManagedBean().getQt());
				setUserDetails(qtCommitte);
				qtCommitte.setCommitteeActivities(null);
				qtCommitte.setQtCommitteRemarks(null);
				qtCommitte.setScQtMeetings(null);
				ScCommittee committee = new ScCommittee();
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					committee.setApprovalReq(true);
				}
				setUserDetails(committee);

				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
				qtCommitte.setScCommittee(committee);
			}
		} finally {
			committeSO = null;
		}
		return qtCommitte;
	}

	/**
	 * Approver setting dtls.
	 * 
	 * @param code
	 *            the code
	 * @param approval
	 *            the approval
	 * @return the dual list model
	 */
	private DualListModel<ScQtApproverSettings> approverSettingDtls(
			RoleCode code, Long orgProfileId, ScQtApproval approval) {
		List<ScQtApproverSettings> availableApprovers = new ArrayList<ScQtApproverSettings>();
		List<ScQtApproverSettings> selectedApprovers = new ArrayList<ScQtApproverSettings>();
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(code.value());
		userReqDto.setOrgProfileId(orgProfileId);
		userReqDto.setWithProfileGroup(true);
		UserResDTO userResDTO = iUserSO
				.retrieveUserListWithProfileGroupUserByRole(userReqDto);
		List<PmUser> approvers = userResDTO.getUserList();
		// Remove Current DO from approver list
		removeDeskOfficer(approvers);
		if (null != approval) {
			if (null != approval.getApproverSettings()
					&& !approval.getApproverSettings().isEmpty()) {
				boolean isApproverExists;
				for (PmUser pmUser : approvers) {
					isApproverExists = false;
					for (ScQtApproverSettings scQtApproverSettings : approval
							.getApproverSettings()) {
						if (scQtApproverSettings.getPmUser().getUserId()
								.equals(pmUser.getUserId())) {
							scQtApproverSettings.setScQtApproval(approval);
							isApproverExists = true;
							break;
						}
					}
					if (!isApproverExists) {
						ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
						qtApproverSettings.setPmUser(pmUser);
						qtApproverSettings.setScQtApproval(approval);
						availableApprovers.add(qtApproverSettings);
					}
				}
				selectedApprovers = approval.getApproverSettings();
			} else {
				for (PmUser pmUser : approvers) {
					ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
					qtApproverSettings.setPmUser(pmUser);
					qtApproverSettings.setScQtApproval(approval);
					availableApprovers.add(qtApproverSettings);
				}
				selectedApprovers = new ArrayList<ScQtApproverSettings>();
			}
		}
		return new DualListModel<ScQtApproverSettings>(availableApprovers,
				selectedApprovers);
	}

	/**
	 * Technical spec edit action.
	 * 
	 * @param checklist
	 *            the checklist
	 * @return the string
	 */
	public String technicalSpecEditAction(ScQtCheckList checklist) {
		this.getManagedBean().setQtCheckList(checklist);
		this.getManagedBean().setQtDoc(checklist.getScQtDoc());
		TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(checklist.getScQtDoc()
				.getTplSpecMaster());
		templateSpecBean
				.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		specBB.retrieveTemplateSpecMaster(templateSpecBean);
		return "/xhtml/sc/quatationtender/quotationTenderDetails/quotationTenderTechnicalTemplateMaintenance.xhtml?faces-redirect=true";
	}

	/**
	 * Gets the local goods radio values.
	 * 
	 * @param itemType
	 *            the item type
	 * @return the local goods radio values
	 */
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);
		types.add(ParameterConst.IMPORTED_GOODS);
		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {
						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} finally {

		}
		return items;
	}

	/**
	 * Technical spec back action.
	 * 
	 * @return the string
	 */
	public String technicalSpecBackAction() {
		debugLogger("technicalSpecBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Financial spec edit action.
	 * 
	 * @param checklist
	 *            the checklist
	 * @return the string
	 */
	public String financialSpecEditAction(ScQtCheckList checklist) {
		this.getManagedBean().setQtCheckList(checklist);
		this.getManagedBean().setQtDoc(checklist.getScQtDoc());

		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO templateresDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		checklist.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(checklist.getScQtDoc()
				.getTplSpecMaster());
		if (this.getManagedBean().getQt().isZonal()
				&& this.getManagedBean().getQt().getQtType()
						.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {

			retrieveQuationTenderDetails(this.getManagedBean().getQt()
					.getQtId());
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
					.getQt().getScQtSupplierCriteria();
			List<TplSpecZoneDtl> businessOwnerZone = this.getManagedBean()
					.getTemplateSpecZoneDtls();

			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
				// TODO Code needs to be remove
				// setUserDetails(qtSupplierCriteria.getQtZoneCriterias());

				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					supplierCriteria.getQtZoneCriterias().setModified(true);
					tplSpecZoneDtl.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());

				}
				templateSpecBean.getSpecMaster().setTplSpecZone(
						supplierCriteria.getQtZoneCriterias());
				templateSpecBean.getSpecMaster().getTplSpecZone()
						.setTplSpecZoneDtls(businessOwnerZone);
			}

		}

		return "/xhtml/sc/quatationtender/quotationTenderDetails/quotationTenderFinancialTemplateMaintenance.xhtml?faces-redirect=true";
	}

	/**
	 * Financial spec back action.
	 * 
	 * @return the string
	 */
	public String financialSpecBackAction() {
		debugLogger("financialSpecBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		if (isQTMaintenanceScreen()) {
			recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
		}
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	/**
	 * Change secretary comm type.
	 * 
	 * @param event
	 *            the event
	 */
	public void changeSecretaryCommType(AjaxBehaviorEvent event) {
		debugLogger("changeSecretaryCommType");
		Long id = (Long) event.getComponent().getAttributes().get("value");
		if (id != null) {
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(id);
			ParameterResDTO parameterResDTO = getParameterSO()
					.retrieveParameterById(parameterReqDTO);

			getManagedBean().getQt().setFinalizationCommitte(
					parameterResDTO.getPmParameter());
			getManagedBean().getQt().setFinalizationSecratery(new PmUser());
		}
	}
	
	/**
	 * Change FC secretary user dropdown.
	 * 
	 * @param event
	 *            the event
	 */
	public void changeFCSecretaryUser(AjaxBehaviorEvent event) {
		debugLogger("changeFCSecretaryUser");
		Long userId = (Long) event.getComponent().getAttributes().get("value");
		this.getManagedBean().setChangesOnQTMaintenance(false);
		if (userId != null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				
				UserReqDTO resDTO = new UserReqDTO();
				resDTO.setUserId(userId);
				PmUser fcSecretaryUser = userSO.retrieveUserByUserId(resDTO)
						.getPmUser();
				
				getManagedBean().getQt().setFinalizationSecratery(fcSecretaryUser);
				this.getManagedBean().setChangesOnQTMaintenance(
						this.getManagedBean().isMaintanceFlow());
			} finally {
				userSO = null;
			}
		}
	}

	/**
	 * Retrieve pm user secratry dtl.
	 * 
	 * @param committeeType
	 *            the committee type
	 * @return the list
	 */
	public List<PmUser> retrievePmUserSecratryDtl(String committeeType) {
		debugLogger("retrievePmUserSecratryDtl");
		this.getManagedBean().setPmUsersList(null);
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmOrgProfile(getManagedBean().getQt().getPmOrgProfile());
			reqDTO.setCommitteeType(committeeType);

			CommitteResDTO resDTO = committeSO
					.retrieveFcSecretaryUsersByCommType(reqDTO);
			this.getManagedBean().setPmUsersList(resDTO.getUserList());
			return resDTO.getUserList();

		} finally {
			committeSO = null;
		}
	}
	
	/**
	 * Committe tab change action.
	 */
	public void committeTabChangeAction(int selectedCommitteeTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("Committe Tab change Action");
		}

		// this.getManagedBean().setToBeDelCommitteMemebers(null);
		this.getManagedBean().setSelectedTabIndex(selectedCommitteeTabIndex);
		this.getManagedBean().setSelectedCommitteeTabIndex(
				selectedCommitteeTabIndex);
		TabBean committeTabBean = this.getManagedBean()
				.getScCommitteeMemberTabList().get(selectedCommitteeTabIndex);

		this.getManagedBean().setSelectedTabBean(committeTabBean);
	}

	/**
	 * Approval tab change action.
	 */
	public void approvalTabChangeAction(int selectedApprovalTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("ApprovalTabChange Action");
		}

		TabBean approvalTabBean = this.getManagedBean().getTabList()
				.get(selectedApprovalTabIndex);

		this.getManagedBean().setSelectedApprovalTabIndex(
				selectedApprovalTabIndex);
		this.getManagedBean().setSelectedApprovalTabBean(approvalTabBean);
		this.getManagedBean()
				.setScQtApproval(approvalTabBean.getScQtApproval());
	}

	/**
	 * Validate qt details.
	 */
	public void validateQtDetails() {
		
		debugLogger("validateQtDetails");
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		int activeIndex = this.getManagedBean().getActiveIndex();

		int subTabIndex = this.getManagedBean().getSelectedTabIndex();

		String taskName = this.getManagedBean().getTaskName();
		boolean isCreateQt = this.getManagedBean().isCreateQt();
		boolean isMaintFlow = this.getManagedBean().isMaintanceFlow();
		String zonelocationLevel = this.getManagedBean().getStateCodes();
		String locationLevel = this.getManagedBean().getLocalLevelCode();
        boolean readOnly = this.getManagedBean().isReadOnly();
		boolean isCommitteeAppointment = this.getManagedBean().isCommitteeAppointment();
		boolean isCommitteeAppointmentRemark = this.getManagedBean().isCommitteeAppointmentRemark();

		QuationTenderResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Object payLoad = this.getManagedBean().getPayLoad();
		Long currentStatus = this.getManagedBean().getCurrentStatusId();
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean()
				.getTemplateSpecZoneDtls();
		QuationTenderReqDTO reqDTO = prepareQTInfo();
		reqDTO.setSaveFlow(true);
		reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
		int changedIndexNumber = manipulateIndexNumber(activeIndex);
		switch (changedIndexNumber) {
		case 0:
			detailSO.validateateQTDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewQTSesionObject();
			this.getManagedBean().setTaskName(taskName);
			retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId());
			setAmmendMentBean(resDTO.getAmendMentBean());
			break;
		case 1:
			detailSO.validateSupplierQualificationDtls(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			retrieveSuppQualificationTabDtl();
			setAmmendMentBean(resDTO.getAmendMentBean());
			break;
		case 2:
			// Skip the validation for committee while save - 10266
			resDTO = detailSO.saveQTDtl(reqDTO);

			int committeeTabIndex = this.getManagedBean()
					.getSelectedCommitteeTabIndex();
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setSelectedTabIndex(subTabIndex);
			// this.getManagedBean().setActiveIndex(0);
			this.getManagedBean().setSelectedCommitteeTabIndex(
					committeeTabIndex);
			retrieveCommitteeAppointmentTabDtl(false, REFORM_NO_ACTION);
			this.getManagedBean().setCurrentStatusId(currentStatus);
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
				initializeRemarksForCommitteeApproval();
			}
			setAmmendMentBean(resDTO.getAmendMentBean());
			break;
		case 3:
			reqDTO.getQtdetail().setModified(false);
			reqDTO.getQtdetail().setFCApproval(true);
			detailSO.validateApprovalDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			int selectedApprovalTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			this.getManagedBean().setSelectedApprovalTabIndex(selectedApprovalTabIndex);
			setAmmendMentBean(resDTO.getAmendMentBean());
			retrieveApprovalTabDtl();
			break;
		case 4:
			if (isinvokeValidations(getManagedBean().getQt())) {
				detailSO.validateBsvDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveBriefingSitevisitTabDtl();
			} else if(QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(currentStatus)){
				// save scQt info onclick of Activites tab
				detailSO.validateateQTDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setTaskName(taskName);
				retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId());
				retrieveActivitiesTabDtl();
				setAmmendMentBean(resDTO.getAmendMentBean());
			}
			this.getManagedBean().setCurrentStatusId(currentStatus);
			break;
		case 5:
			if (isinvokeValidations(getManagedBean().getQt())) {
				detailSO.validateScheduleDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveScheduleTabDtl();
			}
			break;
		case 6:
			if (isinvokeValidations(getManagedBean().getQt())||isMaintancePendingChklistStatus()) {
				detailSO.validateDeductionDtls(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDeductionPaymentTabDtl();
			}
			break;
		case 7:
			if (isinvokeValidations(getManagedBean().getQt())||isMaintancePendingChklistStatus()) {
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDocumentTabDtl();
				break;
			}
		case 8:
			break;
		case 9:
			if (isQTStatusGreaterThan(getManagedBean().getQt().getQtStatus().getStatusId(), QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
				reqDTO.getQtdetail().setModified(true);
				reqDTO.getQtdetail().setFCApproval(true);
				detailSO.validateApprovalDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrievePublicationApprovalTabDtl();
			}
			break;
		}
		
		if (activeIndex != 0) {
			ScQt scQt = getManagedBean().getQt();
			
			if (scQt.isZonal() && 
					scQt.getScQtSupplierCriteria() != null &&
					scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
				// Populate zone detail into tree node
				populateZoneDetailIntoTreeNode(scQt, 
						scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls());	
			}
		}
		if(null!=this.getManagedBean().getCtContract()){
			this.getManagedBean().getCtContract().setContractNo(this.getManagedBean().getQt().getExistingContractNo());
		}
		this.getManagedBean().setActiveIndex(activeIndex);
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setStateCodes(zonelocationLevel);
		this.getManagedBean().setLocalLevelCode(locationLevel);
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		this.getManagedBean().setTaskName(taskName);
		this.getManagedBean().setCommitteeAppointment(isCommitteeAppointment);
		this.getManagedBean().setCommitteeAppointmentRemark(isCommitteeAppointmentRemark);
		this.getManagedBean().setPayLoad(payLoad);
		this.getManagedBean().setCreateQt(isCreateQt);
		this.getManagedBean().setMaintanceFlow(isMaintFlow);
		this.getManagedBean().setReadOnly(readOnly);
		if (isCreateQt) {
			this.getManagedBean().setOutComes(
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.btn.submit",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}
	}

	/**
	 * Qt details tab change.
	 * 
	 * @param activeIndex
	 *            the active index
	 */
	public void qtDetailsTabChange(Integer activeIndex) {
		int priviousIndex = this.getManagedBean().getActiveIndex();
		getManagedBean().getQt().setMitiApproval(false);
		getManagedBean().getQt().setOtherApproval(false);
		getManagedBean().getQt().setFCApproval(false);
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = prepareQTInfo();
		boolean isReadOnly = getManagedBean().isReadOnly();
		boolean isMaintancePendingChklistStatus = isMaintancePendingChklistStatus();
		
		if (priviousIndex != activeIndex) {
			if (!isReadOnly) {
				switch (priviousIndex) {
				case 0:
					detailSO.validateateQTDetails(reqDTO);
					// retrieveQuationTenderDetails(this.getManagedBean().getQt().getQtId());
					break;
				case 1:
					detailSO.validateSupplierQualificationDtls(reqDTO);
					break;

				case 2:
					// retrieveCommitteeAppointmentTabDtl();
					// retrieveSuppQualificationTabDtl();
					break;
				case 3:
						if (isQTStatusBtwScheduleDocAndBeforePublished(getManagedBean()
								.getQt().getQtStatus().getStatusId())) {
							reqDTO.getQtdetail().setFCApproval(true);
						}
						
					detailSO.validateApprovalDetails(reqDTO);
					// retrieveApprovalTabDtl();
					break;

				case 4:
					
					if (!isMaintancePendingChklistStatus && isinvokeValidations(getManagedBean().getQt())) {
						detailSO.validateBsvDetails(reqDTO);
					} else if(isMaintancePendingChklistStatus){
							detailSO.validateDeductionDtls(reqDTO);
					}
					break;

			case 5:
				if (!this.getManagedBean().isMaintanceFlow()) {
					Long currentStatusId = getManagedBean().getQt().getQtStatus().getStatusId();
					if (isQTStatusBtwScheduleDocAndBeforePublished(currentStatusId)) {
							detailSO.validateScheduleDetails(reqDTO);
						}
					}
				
					break;

				case 6:
					if (isinvokeValidations(getManagedBean().getQt())) {
						detailSO.validateDeductionDtls(reqDTO);
					}
					break;

			case 7:
				break;
			case 8:
				break;
			case 9:
				if (!this.getManagedBean().isMaintanceFlow()) {
					if (isQTStatusBtwScheduleDocAndBeforePublished(getManagedBean().getQt().getQtStatus().getStatusId())) {

							reqDTO.getQtdetail().setCallPTJValidation(true);
							reqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
							detailSO.validateApprovalDetails(reqDTO);
						}
					}
					break;
				}
			}
			switch (activeIndex) {
			case 0:
				// detailSO.validateateQTDetails(reqDTO);
				retrieveQuationTenderDetails(this.getManagedBean().getQt()
						.getQtId());
				break;
			case 1:
				retrieveSuppQualificationTabDtl();
				break;

			case 2:
				// detailSO.validateSupplierQualificationDtls(reqDTO);
				retrieveCommitteeAppointmentTabDtl(false, REFORM_NO_ACTION);
				break;
			case 3:
				// detailSO.validateApprovalDetails(reqDTO);
				retrieveApprovalTabDtl();
				break;

			case 4:
				Long statusId = getManagedBean().getQt().getQtStatus().getStatusId();
				if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statusId) ||
						QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(statusId)) {
					retrieveActivitiesTabDtl();
				} else if(isMaintancePendingChklistStatus){
					retrieveDeductionPaymentTabDtl();
				} else {
					retrieveBriefingSitevisitTabDtl();
				}
				break;
			case 5:
				if(isMaintancePendingChklistStatus){
					retrieveDocumentTabDtl();
				} else{
					retrieveScheduleTabDtl();
				}
				
				break;
			case 6:
				if(isMaintancePendingChklistStatus){
					retrieveActivitiesTabDtl();
				} else{
					retrieveDeductionPaymentTabDtl();
				}
				
				break;
			case 7:
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())) {
					retrieveDocumentTabDtl();
				}
				break;
			case 8:
				retrieveActivitiesTabDtl();
				break;
			case 9:
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())) {
					retrievePublicationApprovalTabDtl();
				}
				break;
			}
			this.getManagedBean().setActiveIndex(activeIndex);
		}
	}
	
	private boolean isQTStatusBtwScheduleDocAndBeforePublished(Long currentStatusId) {
		return isQTStatusInBetween(currentStatusId,
				QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT,
				QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
	}
	
	private boolean isQTStatusGreaterThanOrEqualsScheduleDoc(Long currentStatusId) {
		return isQTStatusGreaterThanOrEquals(currentStatusId,
				QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
	}

	/**
	 * Check is approval exists.
	 * 
	 * @param qtApproval
	 *            the qt approval
	 * @param existingApprovalTabs
	 *            the existing approval tabs
	 * @return the sc qt approval
	 */
	private ScQtApproval checkIsApprovalExists(ScQtApproval qtApproval,
			List<TabBean> existingApprovalTabs) {
		if (null != existingApprovalTabs) {
			for (TabBean approvalTab : existingApprovalTabs) {
				if (qtApproval
						.getApprovalCategory()
						.getParameterCode()
						.equalsIgnoreCase(
								approvalTab.getScQtApproval()
										.getApprovalCategory()
										.getParameterCode())) {
					qtApproval.setApproverSettings(approvalTab
							.getScQtApproval().getApproverSettings());

					qtApproval.setScQtApprovalRemarks(approvalTab
							.getScQtApproval().getScQtApprovalRemarks());
					List<ScQtApprovalRemark> approvalRemarks = qtApproval
							.getScQtApprovalRemarks();
					if (CollectionUtils.isNotEmpty(approvalRemarks)) {
						for (ScQtApprovalRemark remarks : approvalRemarks) {
							remarks.setScQtApproval(qtApproval);
						}

					}
					List<ScQtApprovalAttachment> approvalAttachements = approvalTab
							.getScQtApproval().getScQtApprovalAttachments();
					if (CollectionUtils.isNotEmpty(approvalTab
							.getScQtApproval().getScQtApprovalAttachments())) {
						for (ScQtApprovalAttachment approvalAttachment : approvalAttachements) {

							approvalAttachment.setScQtApproval(qtApproval);
						}
						qtApproval
								.setScQtApprovalAttachments(approvalTab
										.getScQtApproval()
										.getScQtApprovalAttachments());
					}
					return qtApproval;
				}
			}
		}
		return qtApproval;
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Construct bsv by bean.
	 * 
	 * @param bsvDtlBean
	 *            the bsv dtl bean
	 * @return the sc qt bsv
	 */
	private ScQtBsv constructBsvByBean(BriefingZoneBean bsvDtlBean) {
		ScQtBsv scQtBsv = null;
		if (null != bsvDtlBean) {
			scQtBsv = bsvDtlBean.getScQtBsv();
			List<ScQtBsvDtl> bsvDetils = new ArrayList<ScQtBsvDtl>();
			List<ScQtBsvDtl> briefings = bsvDtlBean.getBriefingList();
			List<ScQtBsvDtl> siteVisit = bsvDtlBean.getSitevisitList();

			if (!scQtBsv.isSameBsv()) {
				if (!CollectionUtils.isEmpty(briefings)) {
					bsvDetils.addAll(briefings);
				}
				if (!CollectionUtils.isEmpty(siteVisit)) {
					bsvDetils.addAll(siteVisit);
					if (bsvDetils.size() >= 2) {
						if (!scQtBsv.isAllSiteVisitReq()) {
							if (scQtBsv.getMinSiteVisitAttend() < 1) {
								throw new EpException(MessageConst.ERROR_760273);
							}
						}
					}
				}
			} else {
				if (!CollectionUtils.isEmpty(briefings)) {
					bsvDetils.addAll(briefings);
					scQtBsv.setAllSiteVisitReq(false);
					scQtBsv.setMinSiteVisitAttend(1);
				}
			}
			/*
			 * if (!scQtBsv.isAllBriefingReq()) { if
			 * (scQtBsv.getMinBriefingAttend() < 1) { throw new EpException(
			 * "Min Briefing Attendance is greater than 0"); } }
			 */

			scQtBsv.setScQtBsvDtls(bsvDetils);
		}
		return scQtBsv;
	}

	/**
	 * Checks if is empty or null.
	 * 
	 * @param list
	 *            the list
	 * @return true, if is empty or null
	 */
	private boolean isEmptyOrNull(List<? extends Object> list) {
		if (null == list || list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	public String submitQtDetailsFormMaintanace() {
		log.info("submitQtDetailsFormMaintanace");
		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			updateApprovalDetails(this.getManagedBean().getQt());
			retrieveApprovalTabDtl();

			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setSaveFlow(false);
			ScQt qtDetails = quationTenderReqDTO.getQtdetail();

			quationTenderReqDTO.setQtdetail(qtDetails);
			quationTenderReqDTO.setAmendMentBean(this.getManagedBean()
					.getAmendMentBean());
			QuationTenderResDTO quationTenderResDTO = detailSO
					.updateQtMaintananceDetails(quationTenderReqDTO);
			ScQt scQt = quationTenderResDTO.getQtDetail();
			Long currentStatusId = qtDetails.getQtStatus().getStatusId();
			
			if (QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION.equals(currentStatusId)) {
				Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
				if (!DateUtils.isDateGreater(DateUtils.setDefaultDateTime(scQt.getPublishDate()), todayDate)) {
					try {
						log.info("call simple scheduler if QT is publishing today.");
						ScSchedulerHelper.getInstance().scheduleSimpleQTPublishingJob(scQt);
					} catch (SchedulerException e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}
				} 
			} else if (QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE.equals(currentStatusId)) {
				Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
				if (!DateUtils.isDateGreater(DateUtils.setDefaultDateTime(scQt.getClosingDate()), todayDate)) {
					try {
						log.info("call simple scheduler if QT is closing today.");
						ScSchedulerHelper.getInstance().scheduleSimpleQTClosingJob(scQt);
					} catch (SchedulerException e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}
				} 
			}
			
			String msgCode;
			if (quationTenderResDTO.isSuccessful()) {
				msgCode = MessageConst.INFO_700026;
			} else {
				msgCode = MessageConst.INFO_700004;
			}
			
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													msgCode,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.DOCUMENT_TYPES,
																	ParameterConst.DOCTYPE_QUOTATION),
															scQt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;

		} finally {
			detailSO = null;
		}

	}

	/**
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	public String cancelQtDetails() {
		debugLogger("cancelQtDetails");
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();

			if (isQTStatusGreaterThanOrEquals(this.getManagedBean().getCurrentStatusId(), QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION)) {
				quationTenderReqDTO.setQtId(scQt.getQtId());
				quationTenderReqDTO
						.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalsByQtIdNType(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQtApproval qtApproval = resDTO.getQtApproval();
				
				List<ScQtApprovalRemark> qtRemarks = initializeRemarks(qtApproval, 
						this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(),
						qtApproval.getScQtApprovalRemarks(), ScQtApprovalRemark.class);
				for(ScQtApprovalRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(false);
				}
				qtApproval.setScQtApprovalRemarks(qtRemarks);
				TabBean tabBean = new TabBean(null, null, qtApproval,
						retrieveFinalizationCommitteeType());
				this.getManagedBean().setSelectedApprovalTabBean(tabBean);
			} else {
				scQt.setScQtRemarks(initializeRemarks(scQt, this
						.getManagedBean().getQt().getCurrentWorkFlowStatus(),
						scQt.getScQtRemarks(), ScQtRemark.class));
			}
			return "/xhtml/sc/quatationtender/cancelqtdetails.xhtml";

		} finally {

		}

	}

	/**
	 * Change pb secretary comm type.
	 * 
	 * @param event
	 *            the event
	 */
	public void changePbSecretaryCommType(ScQtApproverSettings approverSettings) {
		debugLogger("changePbSecretaryCommType");
		
		Long commTypeId = approverSettings.getCommitteeType().getParameterId();
		if (null != commTypeId) {
			PmParameter commTypeCode = ParameterHelper.getInstance().getPmParameterById(commTypeId);
			approverSettings.setCommitteeType(commTypeCode);
			
			getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
					.getApproverSettings().get(0).setPmUser(new PmUser());
		}
	}

	/**
	 * Submit qt cancel details.
	 * 
	 * @return the string
	 */
	public String submitQtCancelDetails() {

		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class); //

			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt qt = this.getManagedBean().getQt();
			String ackMsg = null;
			if (isQTStatusPublished(getManagedBean().getCurrentStatusId())) {
				qt.setScQtCommittees(null);
				qt.setScQtBriefings(null);
				qt.setScQtStatus(null);

				ScQtApproval approval = this.getManagedBean()
						.getSelectedApprovalTabBean().getScQtApproval();
//				finalizeRemarks(approval.getScQtApprovalRemarks());
				List<ScQtApprovalRemark> remarks = approval.getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { 
										ResourceBundleUtils.getMessage(
											"lbl.sc.remark", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											new FacesScopes().getUserLocale()) });
						}
					}
				}

				List<ScQtApproverSettings> approverSettings = approval
						.getApproverSettings();
				PmParameter fcCommTypeCode = null;
				PmUser approver = null;
				if (null != approverSettings && !approverSettings.isEmpty()) {
					fcCommTypeCode = approverSettings.get(0).getCommitteeType();
					approver = approverSettings.get(0).getPmUser();
				}
				validateFCSecretaryDtl(fcCommTypeCode, approver);

				List<ScQtApproval> scQtApprovals = new ArrayList<ScQtApproval>();

				scQtApprovals.add(approval);
				qt.setScQtApprovals(scQtApprovals);
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setPmUser(new FacesScopes()
						.getUserSessionBean());
				quationTenderReqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
				
				ackMsg = MessageUtils
						.getMessage(
								MessageConst.INFO_700052,
								new String[] {
										getParameterDesc(
												ParameterTypeConst.DOCUMENT_TYPES,
												ParameterConst.DOCTYPE_QUOTATION),
										qt.getQtNo() },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes()
										.getUserLocale());
			} else {
				quationTenderReqDTO.setQtId(this.getManagedBean().getQt()
						.getQtId());
				List<ScQtRemark> remarks = qt.getScQtRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { 
										ResourceBundleUtils.getMessage(
											"lbl.sc.remark", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											new FacesScopes().getUserLocale()) });
						}
					}
				}
				finalizeRemarks(qt.getScQtRemarks());
				qt.setScQtStatus(null);
				qt.setScQtCommittees(null);
				qt.setScQtApprovals(null);
				qt.setScQtBriefings(null);
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setPmUser(new FacesScopes()
						.getUserSessionBean());
				
				ackMsg = MessageUtils
						.getMessage(
								MessageConst.INFO_700031,
								new String[] {
										getParameterDesc(
												ParameterTypeConst.DOCUMENT_TYPES,
												ParameterConst.DOCTYPE_QUOTATION),
										qt.getQtNo() },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes()
										.getUserLocale());

			}
			QuationTenderResDTO quationTenderResDTO = detailSO
					.cancleQT(quationTenderReqDTO);

			if (quationTenderResDTO.isSuccessful()) {
				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								ackMsg));
				
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
			
			return null;

		} finally {
			detailSO = null;
		}

	}
	
	private void validateFCSecretaryDtl(PmParameter fcCommTypeCode, PmUser fcSecretary) {
		if (fcCommTypeCode == null || fcCommTypeCode.getParameterId() == null) {
			throw new EpException(
					MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils
							.getMessage(
									"lbl.sc.finalization.committee",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes()
											.getUserLocale()) });
		}

		if (fcSecretary == null || fcSecretary.getUserId() == null) {
			throw new EpException(
					MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils
							.getMessage(
									"lbl.sc.secretary.name",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes()
											.getUserLocale()) });
		}
	}

	/**
	 * Gets the msg code by task name.
	 * 
	 * @param taskName
	 *            the task name
	 * @return the msg code by task name
	 */
	private String getMsgCodeByTaskName(String taskName) {
		if (SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName)) {
			return MessageConst.INFO_700052;
		}
		return MessageConst.INFO_700005;
	}

	/**
	 * Update user id to settings.
	 * 
	 * @param event
	 *            the event
	 */
	public void updateUserIdToSettings(AjaxBehaviorEvent event) {
		Long id = (Long) event.getComponent().getAttributes().get("value");
		List<PmUser> userList = this.getManagedBean().getPmUsersList();
		for (PmUser user : userList) {
			if (user.getUserId().equals(id)) {
				this.getManagedBean().getSelectedApprovalTabBean()
						.getScQtApproval().getApproverSettings().get(0)
						.setPmUser(user);
				break;
			}
		}
	}

	/**
	 * Enable field by status n filed name.
	 * 
	 * @param fieldName
	 *            the field name
	 * @return true, if successful
	 */
	public boolean enableFieldByStatusNFiledName(String fieldName) {
		Long statusId = this.getManagedBean().getCurrentStatusId();

		return enableFieldByStatusNFiledName(fieldName, statusId, 
				getManagedBean().isMaintanceFlow(), 
				!getManagedBean().isReadOnly());

	}
	
	private boolean enableFieldByStatusNFiledName(String fieldName,
			Long statusId, boolean isMaintance, boolean isEditable) {

		List<String> values = QTFiledMapper.getEnableFieldsByStatus(statusId,
				isMaintance, isEditable);
		if (CollectionUtils.isNotEmpty(values)) {
			if (values.contains(fieldName)) {
				return false;
			}

		}
		return true;

	}

	/**
	 * Gets the qt approval by category.
	 * 
	 * @param approvals
	 *            the approvals
	 * @param approvalCategory
	 *            the approval category
	 * @return the qt approval by category
	 */
	private ScQtApproval getQtApprovalByCategory(List<ScQtApproval> approvals,
			String approvalCategory) {
		if (CollectionUtils.isNotEmpty(approvals)) {
			for (ScQtApproval scQtApproval : approvals) {
				if (approvalCategory.equalsIgnoreCase(scQtApproval
						.getApprovalCategory().getParameterCode())) {
					return scQtApproval;

				}
			}
		}
		return null;
	}

	private void setAmmendMentBean(QtAmendMentBean amendMentBean) {
		if (null != amendMentBean) {
			this.getManagedBean().setAmendMentBean(amendMentBean);
		}
	}

	private boolean isinvokeValidations(ScQt scQt) {

		PmStatus pmStatus = scQt.getQtStatus();
		if (null != pmStatus) {
			Long currentStatusId = pmStatus.getStatusId();
			if (isQTStatusInBetween(currentStatusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION)
					&& !this.getManagedBean().isMaintanceFlow()) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Checks if is approval request exists.
	 * 
	 * @param qtApproval
	 *            the qt approval
	 * @param requestType
	 *            the request type
	 * @return true, if is approval request exists
	 */
	private boolean isApprovalRequestExists(ScQtApproval qtApproval) {

		if (null != qtApproval) {
			List<ScQtApprovalRequest> approvalRequests = qtApproval
					.getScQtApprovalDtls();
			if (CollectionUtils.isNotEmpty(approvalRequests)) {
				for (ScQtApprovalRequest approvalRequest : approvalRequests) {

					if (null != approvalRequest.getSubmissionDate()
							&& null == approvalRequest.getApproverAction()) {

						return true;
					}
				}
			}

		}

		return false;
	}

	/**
	 * Enable field by status n filed name.
	 * 
	 * @param fieldName
	 *            the field name
	 * @return true, if successful
	 */
	public boolean enableFieldByStatus(String fieldName) {
		Long statusId = this.getManagedBean().getCurrentStatusId();

		Boolean isFieldToBeEnabled = enableFieldByStatusNFiledName(fieldName);
		List<ScQtApproval> approvals = this.getManagedBean().getQt()
				.getScQtApprovals();

		ScQtApproval approval = null;

		if (!isFieldToBeEnabled) {
			if (isStatusOfBpkOrScheduleRevise(statusId)
					|| isQTStatusGreaterThanOrEquals(statusId,QTStatusConst.QT_PROPOSAL_PREPARATION)) {

				approval = getQtApprovalByCategory(approvals,
						ParameterConst.APPROVAL_CATEGORY_BPK);
				return isApprovalRequestExists(approval);

			} else {
				boolean isEditable = false;
				approval = getQtApprovalByCategory(approvals,
						ParameterConst.APPROVAL_CATEGORY_PTJ);
				isEditable = isApprovalRequestExists(approval);
				if (!isEditable) {
					approval = getQtApprovalByCategory(
							approvals,
							ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
					isEditable = isApprovalRequestExists(approval);
				}
				return isEditable;
			}

		}

		return isFieldToBeEnabled;

	}

	public boolean approvalDtlsEditable(ScQtApproval approval) {
		
		Long statusId = this
				.getManagedBean().getQt().getQtStatus().getStatusId();
		if (this.getManagedBean().isMaintanceFlow()
				&& QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(statusId)) {
			return true;
		}
		if ((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || 
				isStatusOfBpkOrScheduleRevise(statusId)) 
				&& (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode()) || (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode())))) {
			return (this.getManagedBean().isReadOnly());
		}
		List<ScQtApprovalRequest> approvalRequests = approval
				.getScQtApprovalDtls();
		if (CollectionUtils.isNotEmpty(approvalRequests)) {

			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				if (null == approvalRequest.getSubmissionDate()
						|| (null != approvalRequest.getApproverAction() && ParameterConst.APPROVAL_DECISION_TYPE_REVISED
								.equalsIgnoreCase(approvalRequest
										.getApproverAction().getParameterCode()))) {
					return (this.getManagedBean().isReadOnly());
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		debugLogger("getPTJGroupCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		Long MinistryOrgProfileId = this.getManagedBean().getQtnTenderSearchBean().getMinistryProfile()
				.getOrgProfileId();
		if (null != MinistryOrgProfileId) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
				for (int i = 0; i < orgLevel; i++) {
					condition.append("parentOrgProfile.");
				}
				condition.append("orgProfileId");
				condition.append(SourcingConstants.LOV_CONDITION_SEPARATOR);
				condition.append(MinistryOrgProfileId);

			}

		} 
		return condition.toString();
	}

	/**
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJCondition() {
		debugLogger("getPTJCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		Long MinistryOrgProfileId = qtnTenderSearchBean.getMinistryProfile()
				.getOrgProfileId();
		Long PTJGroupOrgProfileId = qtnTenderSearchBean.getPtjGroupProfile()
				.getOrgProfileId();
		if (null != PTJGroupOrgProfileId) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + PTJGroupOrgProfileId);
		} else if (null != MinistryOrgProfileId) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + MinistryOrgProfileId);
		} 
		return condition.toString();
	}

	/**
	 * The method used set parent profile and reset child profile value
	 * 
	 * @param orgProfile
	 * 		the PmOrgProfile instance
	 */
	public void postUpdateOrgProfileListener(BaseEntity orgProfile) {
		debugLogger("postUpdateOrgProfileListener");
		
		if (orgProfile instanceof PmOrgProfile) {
			PmOrgProfile pmOrgProfile = (PmOrgProfile)orgProfile;
			String orgType = pmOrgProfile.getOrgTypeCode().getParameterCode();
			QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
			
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				PmOrgProfile ministryOrgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				orgTypeCode.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
				ministryOrgProfile.setOrgTypeCode(orgTypeCode);
				qtnTenderSearchBean.setPtjGroupProfile(ministryOrgProfile);
				
				postUpdateOrgProfileListener(ministryOrgProfile);
				
			} else { 
				if (qtnTenderSearchBean.getMinistryProfile() == null || 
						qtnTenderSearchBean.getMinistryProfile().getOrgProfileId() == null) {
					qtnTenderSearchBean.setMinistryProfile(retrieveMinistryOrgProfile(
							pmOrgProfile.getOrgProfileId()));	
				}
				
				if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
					qtnTenderSearchBean.setPtjProfile(new PmOrgProfile());
					
				} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
					if (qtnTenderSearchBean.getPtjGroupProfile() == null || 
							qtnTenderSearchBean.getPtjGroupProfile().getOrgProfileId() == null) {
						qtnTenderSearchBean.setPtjGroupProfile(retrieveJabatanOrgProfile(
								pmOrgProfile.getOrgProfileId()));	
					}
				}
			}
				
		}
	}

	/**
	 * Checks if is Auditor General/Internal user.
	 * 
	 * @return true, if is Auditor General/Internal user
	 */
	private boolean isAuditor() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())
				|| facesScopes.hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}
	
	private boolean isDeskOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value());
	}
	
	private boolean isMofSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value());
	}

	private void updateApprovalRequestDtls(List<ScQtApproval> approval,
			String approvalCategory) {

		if (CollectionUtils.isNotEmpty(approval)) {
			for (int i = 0; i < approval.size(); i++) {

				if (approvalCategory.equalsIgnoreCase(approval.get(i)
						.getApprovalCategory().getParameterCode())) {
					finalizeRemarks(approval.get(i).getScQtApprovalRemarks());
					List<ScQtApprovalRequest> approvalRequests = approval
							.get(i).getScQtApprovalDtls();
					if (CollectionUtils.isNotEmpty(approvalRequests)) {
						for (int j = 0; j < approvalRequests.size(); j++) {
							PmParameter approvalAction = approvalRequests
									.get(j).getApproverAction();
							if (null != approvalAction
									&& ParameterConst.APPROVAL_DECISION_TYPE_REVISED.equalsIgnoreCase(approvalAction
											.getParameterCode())) {
								approvalRequests.get(j).setSubmissionDate(null);
								approvalRequests.get(j).setApproverAction(null);
							}
						}
					}
				}
			}
		}
	}

	private void updateApprovalDetails(ScQt scQt) {
		// Setting the Approval details to Qtdetails
		List<TabBean> tabList = this.getManagedBean().getTabList();

		if (CollectionUtils.isNotEmpty(tabList)) {
			List<ScQtApproval> approvalList = null;
			approvalList = new ArrayList<ScQtApproval>();
			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(
							tabBean.getBudgetApprovalUsers().getTarget());
				}

				tabBean.getScQtApproval().setScQtApprovalRemarks(
						finalizeRemarks(tabBean.getScQtApproval()
								.getScQtApprovalRemarks()));
				// setUserDetails(tabBean.getScQtApproval());

				approvalList.add(tabBean.getScQtApproval());

			}
			scQt.setScQtApprovals(approvalList);
		}

	}

	/**
	 * Set required data for Tracking Dairy info of QT
	 * 
	 * @param scQt
	 */
	private void setScQtTrackingDairyObject(ScQt scQt) {

		Object[] scTdObj = new Object[7];
		scTdObj[0] = scQt.getQtId();
		scTdObj[1] = scQt.getQtNo();
		scTdObj[2] = scQt.getCreatedDate();
		scTdObj[3] = ParameterConst.DOCTYPE_QUOTATION;
		scTdObj[4] = scQt.getQtStatus().getStatusId();
		scTdObj[5] = scQt.getQtTitle();
		this.getManagedBean().setScTdObj(scTdObj);
	}
	
	public boolean isMaintancePendingChklistStatus(){
		
		boolean isPresent = false;
		if (this.getManagedBean().isMaintanceFlow()) {
			Long statusId = this
					.getManagedBean().getQt().getQtStatus().getStatusId();
			if (QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(statusId) ||
					QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT.equals(statusId)	||
						QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF.equals(statusId)||
						QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION.equals(statusId)) {
				isPresent = true;
			}
		}
	return isPresent;	
	}
	
	public int manipulateIndexNumber(int activeIndex){
		
		int changedIndex = activeIndex;
		if (isMaintancePendingChklistStatus()) {
			if (activeIndex == 4) {
				changedIndex = QTConstants.DEDUCTION_TAB_INDEX;
			} else if (activeIndex == 5) {
				changedIndex = QTConstants.DOCUMENTS_TAB_INDEX;
			} else if (activeIndex == 6) {
				changedIndex = QTConstants.ACTIVE_TAB_INDEX;
			} 
		}
		return changedIndex;
		
	}
	
 private boolean isStatusOfBpkOrScheduleRevise(Long statusId) {
		
		boolean isStatusOfBpkOrScheduleRevise = false;
		if (!this.getManagedBean().isMaintanceFlow()) {
			if (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(statusId)
					|| QTStatusConst.QT_PENDING_PB_APPROVAL.equals(statusId)
					|| QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(statusId)) {
				isStatusOfBpkOrScheduleRevise = true;
			}
		}
		return isStatusOfBpkOrScheduleRevise;
	}
 
 /**
	 * Retrieve non zonal dtls.
	 */
	private void retrieveNonZonalDtls() {
		log.info("retrieveNonZonalDtls - START");
		ScQt scQt = this.getManagedBean().getQt();
		if (null != scQt.getZoneLocationLevel()
				&& null != scQt.getZoneLocationLevel()
						.getParameterId()) {
			Long locationLevel = scQt
					.getZoneLocationLevel().getParameterId();
			scQt.getScQtSupplierCriteria()
					.getLocationLevel().setParameterId(locationLevel);
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);
					scQt.getScQtSupplierCriteria().setLocationLevel( paramDesc.getPmParameter());
					
					break;
				}
			}
			
			if (!ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt.getProcurementMode().getParameterCode())) {
				List<ScQtLocCriteria> scQtLocCriteriaList = new ArrayList<ScQtLocCriteria>();
				if (null != getManagedBean().getZoneGeneralTrees()
						&& !getManagedBean().getZoneGeneralTrees().isEmpty()) {
					for (TreeNode treeNode : getManagedBean().getZoneGeneralTrees()) {
						List<ScQtLocCriteria> qtLocCriteriaList = getLocationsList(treeNode);
						scQtLocCriteriaList.addAll(qtLocCriteriaList);
					}
				}
				retrievLocationDtls(scQtLocCriteriaList);	
			}
		}
		log.info("retrieveNonZonalDtls - END");
	}
 
 /**
	 * Retriev location dtls.
	 * 
	 * @param scQtLocCriteriaList
	 *            the sc qt loc criteria list
	 */
 
 
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.info("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(scQtLocCriteriaList);
		getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					getManagedBean().getZoneTrees().add(
							LocationSingleton.getInstance().buildLocationTree(
									null, localStateList, districts, divisions,
									cities, ScQtLocCriteria.class));
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != districts && !districts.isEmpty()) {
				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton
						.getInstance().getUniqueStatesDivisionsDistrictsCities(
								districts, null);
				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
						.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton
						.getInstance().getUniqueStateDistrictsCities(districts,
								null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
						.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null
						&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
								.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
								.keySet();
						if (divisionsDistrictsCitiesKeySet != null
								&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								division.setPmState(state);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
										.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
										.keySet();
								if (districtsCitiesKeySet != null
										&& !districtsCitiesKeySet.isEmpty()) {
									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}
								}
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
				if (statesDistrictsCitiesKeySet != null
						&& !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
								.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								district.setPmState(state);
								districts.add(district);
							}
						}
						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, null,
												districts, divisions, cities,
												ScQtLocCriteria.class));
					}
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != divisions && !divisions.isEmpty()) {
				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton
						.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null
						&& !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();
						List<PmDivision> divisions1 = mapStatesDivision
								.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								division.setPmState(state);
								divisions.add(division);
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
			}
		}
		log.info("retrievLocationDtls - END");
	}
	/**
	 * Gets the locations list.
	 * 
	 * @param zoneLocation
	 *            the zone location
	 * @return the locations list
	 */
	private List<ScQtLocCriteria> getLocationsList(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		for (TreeNode zoneNode : zoneLocation.getChildren()) {
			for (TreeNode stateNode : zoneNode.getChildren()) {
				if (stateNode.getType().equals("state")) {

					TplSpecLoc stateLoc = (TplSpecLoc) stateNode.getData();
					ScQtLocCriteria stateLocCriteria = new ScQtLocCriteria();
					stateLocCriteria.setPmState(stateLoc.getPmState());
					// stateLocCriteria.setPmDistrict(stateLoc.getPmDistrict());
					// stateLocCriteria.setPmDivision(stateLoc.getPmDivision());
					// ScQtLocCriteria locationDTO = (ScQtLocCriteria)
					// zoneLocation.getData();
					results.add(stateLocCriteria);

					for (TreeNode districtNode : stateNode.getChildren()) {
						TplSpecLoc districtLoc = (TplSpecLoc) districtNode
								.getData();
						ScQtLocCriteria districtLocCriteria = new ScQtLocCriteria();
						// districtLocCriteria.setPmState(districtLoc.getPmState());
						districtLocCriteria.setPmDistrict(districtLoc
								.getPmDistrict());
						districtLocCriteria.setPmDivision(districtLoc
								.getPmDivision());
						results.add(districtLocCriteria);
						for (TreeNode divisionCode : districtNode.getChildren()) {
							TplSpecLoc divisionLoc = (TplSpecLoc) divisionCode
									.getData();
							ScQtLocCriteria divisionLocCriteria = new ScQtLocCriteria();
							divisionLocCriteria.setPmDistrict(divisionLoc
									.getPmDistrict());
							results.add(divisionLocCriteria);
						}
					}
				}
			}
		}
		/*
		 * if (zoneLocation.isLeaf()) {
		 * 
		 * } else { for (TreeNode childrendLocationNode :
		 * zoneLocation.getChildren()) {
		 * results.addAll(getLocationsList(childrendLocationNode)); } }
		 */
		return results;
	}
	
	/**
	 * is Allow QT Closed button
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public boolean isAllowQTClosedBtn(ScQt scQt) {
		if (scQt != null && 
				scQt.getQtStatus() != null && 
						scQt.getQtStatus().getStatusId() != null &&
				QTStatusConst.QT_PROPOSAL_PREPARATION.equals(scQt.getQtStatus().getStatusId())) {
			String isAllowQTClosedBtn = EpPropertiesUtils.getInstance()
					.getProperty(EpPropertiesConst.SC_SHOW_BUTTON_CLOSEQT);
			
			if (StringUtils.isNotBlank(isAllowQTClosedBtn)) {
				return Boolean.valueOf(isAllowQTClosedBtn);
			}
		}		
		
		return false;
	}
	
	/**
	 * is Allow QT Publish button
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public boolean isAllowQTPublishBtn(ScQt scQt) {
		if (scQt != null && 
				scQt.getQtStatus() != null && 
						scQt.getQtStatus().getStatusId() != null &&
				QTStatusConst.QT_PENDING_PUBLICATION.equals(scQt.getQtStatus().getStatusId())) {
			String isAllowQTPublishBtn = EpPropertiesUtils.getInstance()
					.getProperty(EpPropertiesConst.SC_SHOW_BUTTON_CLOSEQT);
			
			if (StringUtils.isNotBlank(isAllowQTPublishBtn)) {
				return Boolean.valueOf(isAllowQTPublishBtn);
			}
		}		
		
		return false;
	}
	
	/**
	 * is Allow QT To cancel
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public void checkIsQTAllowCancel() {
		debugLogger("checkIsQTAllowCancel");
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		boolean isQTAwarded = false;
		boolean isBiddingActive = false;
		boolean isQTPendingCancellation = false;
		
		IScWorkFlowStatusSO workFlowStatusSO;
		try {
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QT_CANCELLATION);
			reqDTO.setDocId(scQt.getQtId());

			ScWorkFlowStatus cancellationStatus = workFlowStatusSO.
					retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			
			if(cancellationStatus != null && 
					cancellationStatus.getPmStatus().getStatusId().equals(QTStatusConst.QC_PENDING_CANCELATON)) {
				isQTPendingCancellation = true;
			}
		}
		finally {
			workFlowStatusSO = null;
		}
		
		if(statusId.equals(QTStatusConst.QT_PENDING_LOI) || statusId.equals(QTStatusConst.QT_PENDING_LOA) ||
				statusId.equals(QTStatusConst.QT_SUPPLIER_FINALIZATION) || statusId.equals(QTStatusConst.QT_RE_FINALIZATION)) {
			isQTAwarded = checkIsQTAwarded(scQt);
			if(statusId.equals(QTStatusConst.QT_PENDING_LOI)) {
				isBiddingActive = checkIsBiddingActive(scQt);
			} 
		}
		
		if(isQTAwarded) {
			throw new EpException(MessageConst.ERROR_760425);
		} else if(isBiddingActive) {
			throw new EpException(MessageConst.ERROR_760424);
		} else if(isQTPendingCancellation) {
			throw new EpException(MessageConst.ERROR_760435);
		} else {
			RequestContext.getCurrentInstance().execute("cancelQTDlg.show()");
		}
	}
	
	/**
	 * Trigger to close the supplier prepare proposal.
	 * 
	 * @param qtId
	 * 		the quotation / tender id
	 */
	public void closeProposal(ScQt scQt) {
		log.info("loginId: " + new FacesScopes().getUserSessionBean().getLoginId());
		
		IQTProposalSO qtProposalSO = null;
		try {
			qtProposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			
			Long qtId = scQt.getQtId();

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtId(qtId);
			
			boolean isUpdateSucessful = qtProposalSO.updateProposalWorkFlowStatus(reqDTO)
				.isSuccessful();
			boolean isSucessful = false;
			if(isUpdateSucessful){
				IQTNoticeBoardSO noticeBoardSO = null;
				try{
					noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
					NoticeBoardReqDTO nbReqDTO = new NoticeBoardReqDTO();
					nbReqDTO.setQtId(qtId);
					isSucessful = noticeBoardSO.closeQtAndSaveProposalAmount(nbReqDTO).isSuccessful();
				}catch(Exception e){
					log.info(scQt.getQtNo() + " Close failed");
					throw new EpException(e.getLocalizedMessage());
				}
				
			}
			if (isSucessful) {
				qtProposalSO.closeProposal(reqDTO);
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								scQt.getQtNo() + " Closing Started. Please wait a while for QT closing."
				));
			}

		} finally {
			qtProposalSO = null;
		}
	}
	
	/**
	 * Trigger to publish Quotation / Tender
	 * 
	 * @param qtId
	 * 		the quotation / tender id
	 */
	public void publishQT(ScQt scQt) {
		log.info("loginId: " + new FacesScopes().getUserSessionBean().getLoginId());
		
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			log.info("qtId: " + scQt.getQtId());
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			scQt = qtDetailSO.retriveQTDetails(reqDTO)
					.getQtDetail();
			scQt.setPublishDate(new Date());
			reqDTO.setQtdetail(scQt);
			QuationTenderResDTO resDTO = qtDetailSO.updateQTDetails(reqDTO);
			boolean isSucessful = resDTO.isSuccessful();
			if(isSucessful){
				scQt = resDTO.getQtDetail();
				if(scQt != null) {
					try {
						log.info("call simple scheduler if QT is publishing today.");
						scQt.setPublishDate(new Date());
						ScSchedulerHelper.getInstance().scheduleSimpleQTPublishingJob(scQt);
					} catch (SchedulerException e) {
						log.info(scQt.getQtNo()+ " Publish failed");
						throw new EpException(e.getLocalizedMessage());
					}
					
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									scQt.getQtNo() + " Publishing Started. Please wait a while for QT publishing."
									));
				}
			}
		} finally {
			qtDetailSO = null;
		}
	}
	
	
	/**
	 * Gets the doc type qt loa dropdown values.
	 *
	 * @return the doc type qt loa dropdown values
	 */
	public SelectItem[] getDocTypeQtLOADropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> qtLoaDoctypes = new ArrayList<String>();
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_BIDDING);
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_QUOTATION);
		
		if (isMofSupplier()) {
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL);
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), qtLoaDoctypes));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, searchBean);
		return items;
	}
	
	/**
	 * Remove pmUser if contains loggined in Desk Officer
	 * 
	 * @param approvers
	 */
	private void removeDeskOfficer(List<PmUser> approvers) {

		if (CollectionUtils.isNotEmpty(approvers)) {
			PmUser currentDO = new FacesScopes().getUserSessionBean();
			boolean isDO = false;
			int index = 0;
			for (PmUser pmUser : approvers) {
				if (pmUser.getUserId().equals(currentDO.getUserId())) {
					isDO = true;
					index++;
					break;
				}
			}
			if (isDO) {
				approvers.remove(index);
			}
		}
	}
	
	private boolean checkIsQTAwarded(ScQt scQt) {
		IQTSuppFinalizationSO suppFinalizationSO;
		try {
			boolean isQTAwarded = false;
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			
			List<ScQtRecommendDtl> recommendDtls = suppFinalizationSO.getRecommendDtlByQTandSupplier(reqDTO).
					getScQtRecommendDtlList();
			for (ScQtRecommendDtl recommendDtl 
					: recommendDtls) {
				Long recommendDtlId = recommendDtl.getRecommendDtlId();

				// If LOI awarded and no decision made on LOA (or) 
				// if LOI awarded and LOA awarded,
				// then consider the item as awarded
				if (recommendDtlId != null) {
					if ( (BooleanUtils.isTrue(
							recommendDtl.getLoiAwarded()) && 
							recommendDtl.getLoaAwarded() == null && 
							recommendDtl.getRecordStatusFlg().equals(
									DbValueConst.RECORD_STATUS_ACTIVE)) ||
											(BooleanUtils.isTrue(
													recommendDtl.getLoiAwarded()) && 
													BooleanUtils.isTrue(
															recommendDtl.getLoaAwarded())) ) {
						isQTAwarded = true;
					}
				}
			}
			return isQTAwarded;
		} finally {
			suppFinalizationSO = null;
		}
	}
	
	private boolean checkIsBiddingActive(ScQt scQt) {
		IBiddingSO biddingSO;
		try {
			boolean isBiddingActive = false;
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			
			BiddingReqDTO reqDTO = new BiddingReqDTO();	
			
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(BidStatusConst.BID_COMPLETED);
			statusIds.add(BidStatusConst.BID_CANCELLED);
			
			ISearchBean<ScBidSchedule> bidSchSearchBean = new SearchBean<ScBidSchedule>();
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"scBid.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_QUOTATION));
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"scBid.docId", SearchOperator.OPERATOR_EQUALS.value(), scQt.getQtId()));
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmStatus.statusId", SearchOperator.OPERATOR_NOT_IN.value(), statusIds));
			reqDTO.setBidSchSearchBean(bidSchSearchBean);

			BiddingResDTO resDTO = biddingSO.searchBiddingScheduleList(reqDTO);
			List<ScBidSchedule> list = resDTO.getBidSchResultBean().getResultList();
			if(list.size() > 0 ) {
				isBiddingActive = true;
			}
			return isBiddingActive;
		} finally {
			biddingSO = null;
		}
	}
	
	
	private void removeCommitteeActivityDetails(ScQt scQt){
		IQTMeetingSO meetingso=null;
		try{
		 meetingso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTMeetingSO.class);
		 List<String> committeeType=new ArrayList<String>();
		 if(ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(scQt.getEvaluationType())){
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);
		 }else{
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE); 
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE); 
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE); 
		 }
		 for(String type:committeeType){
		MeetingReqDTO activityReqDTo = new MeetingReqDTO();
		activityReqDTo.setQtId(scQt.getQtId().longValue());
		activityReqDTo.setCommitteType(type);
		MeetingResDTO activityResDTo = meetingso
				.retriveCommitteActivityDtls(activityReqDTo);
		if (null != activityResDTo
				&& !activityResDTo.getCommitteMembers().isEmpty()) {
			MeetingReqDTO deleteActivityDao = new MeetingReqDTO();
			List<ScQtCommitteeActivity> deleteActivityList = new ArrayList<ScQtCommitteeActivity>();
			for (ScQtCommitteeActivity activity : activityResDTo
					.getCommitteMembers()) {
				if (!activity.isBrandNew()) {
					// activity.setScCommitteeMember(null);
					activity.setDeleted(true);
					deleteActivityList.add(activity);
				}
			}
			deleteActivityDao
					.setDeletedCommitteeActivityMembersList(deleteActivityList);
			meetingso.deleteCommitteeActivityDtls(deleteActivityDao);
		}
		}
		}finally{
			
		}
	}
	
}
