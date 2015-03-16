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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

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
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
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
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtAppRequestRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
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
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalCategory;
import my.ep.type.sc.CommitteType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

/**
 * @Author VidyasagarReddy
 */
@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);

	// private FacesContext facesContext = FacesContext.getCurrentInstance();

	public QTDetailBackingBean() {
		log.info("inside constructor");

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	@PostConstruct
	private void init() {
		if (this.getManagedBean().getQt() == null) {
			this.getManagedBean().setQt(new ScQt());
		}
		if (this.getManagedBean().getQt().getPmOrgProfile() == null) {
			this.getManagedBean().getQt().setPmOrgProfile(new PmOrgProfile());
		}
		// handlePTJName();
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		if (searchQTDataModel == null) {
			getManagedBean().setSearchQTDataModel(
					new QTPagingDataModel("searchQTs", getManagedBean()
							.getQtnTenderSearchBean()));
		}
	}

	/**
	 * To add empty row to gosodo table
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

	public void handleDoChange(ScQtBudget qtGosodoBudget) {

	}

	/**
	 * Method to save the QT details
	 */
	public void saveQtDetailsAction() {
		debugLogger("saveQtDetailsAction");
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
		} finally {

		}

	}

	public String submitQTDetailsAction() {
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			validateQtDetails();
			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			quationTenderReqDTO.setToken(ctxToken);
			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			quationTenderReqDTO.setPayLoad(this.getManagedBean()
					.getQuotationTenderDataType());
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
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	/**
	 * Method to search the quationtenders based on serach date
	 */
	public void searchQTAction() {
		getManagedBean().getSearchQTDataModel().reloadData();
	}

	/**
	 * Method for clone search qts
	 */
	public void cloneSearchQtAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	/**
	 * Clone Popup Action - To clear the clone search result for next time
	 */
	public void clonePopupAction() {
		debugLogger("clonePopupAction");
		QtnTenderSearchBean cloneQtnTenderSearchBean = new QtnTenderSearchBean();
		IPagingDataModel<ScQt> cloneSearchQTDataModel = new QTPagingDataModel(
				"cloneQtsearch", cloneQtnTenderSearchBean);
		getManagedBean().setCloneSearchQTDataModel(cloneSearchQTDataModel);
		getManagedBean().setCloneQtnTenderSearchBean(cloneQtnTenderSearchBean);
	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of department.
	 * */
	public void handleMinistryProfileChanged() {
		debugLogger("handleMinistryProfileChanged");
		Long parentOrgProfileId = this.getManagedBean().getQt()
				.getMinistryOrgProfile().getOrgProfileId();
		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				this.getManagedBean().setUserDepartmentList(
						resDTO.getOrgProfileList());
			}
		} else {
			this.getManagedBean().setUserDepartmentList(
					new ArrayList<PmOrgProfile>());
		}
		this.getManagedBean().getQt().getDepartmentOrgProfile()
				.setOrgProfileId(null);
		handleDepartmentProfileChanged();
	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of division.
	 */
	public void handleDepartmentProfileChanged() {
		debugLogger("handleDepartmentProfileChanged");
		Long parentOrgProfileId = this.getManagedBean().getQt()
				.getDepartmentOrgProfile().getOrgProfileId();
		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				this.getManagedBean().setUserDivisionList(
						resDTO.getOrgProfileList());
			}
		} else {
			this.getManagedBean().setUserDivisionList(
					new ArrayList<PmOrgProfile>());
		}
		this.getManagedBean().getQt().getPmOrgProfile().setOrgProfileId(null);
	}

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
	 * 
	 */
	public List<PmOrgProfile> handlePTJName() {
		debugLogger("handleDepartmentProfileChanged");
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null && user.getUserId() != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			// reqDTO.setParentOrgProfileId(parentOrgProfileId);
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
			this.getManagedBean().setUserDivisionList(pmOrgProfiles);
			return pmOrgProfiles;
		}
		return null;
	}

	public void handlePTJNameChanged(PmOrgProfile orgProfile) {
		List<PmOrgProfile> orgProfileList = getManagedBean()
				.getUserDivisionList();
		if (null != orgProfileList) {
			for (PmOrgProfile list : orgProfileList) {
				if (list.getOrgProfileId().equals(orgProfile.getOrgProfileId())) {
					getManagedBean().setOrgProfile(list);
				}
			}
		} else {
			debugLogger("org profile list should not be empty");
		}
	}

	public String gotoQtSearchPage() {
		debugLogger("gotoQtSearchPage");
		this.getManagedBean().setQt(null);
		if (this.getManagedBean().getQt() == null) {
			this.getManagedBean().setQt(new ScQt());
		}
		if (this.getManagedBean().getQt().getPmOrgProfile() == null) {
			this.getManagedBean().getQt().setPmOrgProfile(new PmOrgProfile());
		}
		return QTConstants.SEARCH_PAGE;
	}

	/**
	 * Method to return the maintance page
	 * 
	 * @return maintanacepage
	 */
	public String editAction(ScQt scQt) {
		IQTDetailSO detailSO;
		String REDIRECT_PAGE = QTConstants.MAINTANANCE_PAGE;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			Long qtId = scQt.getQtId();
			renewSesionObject();

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

			reqDTO.setQtId(qtId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			ScQt selectedScQt = resDTO.getQtDetail();

			Long currentStatus = selectedScQt.getQtStatus().getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);
			this.getManagedBean().setQt(selectedScQt);
			if (QTStatusConst.QT_PENDING_APPOINT_BO.equals(currentStatus)
					|| QTStatusConst.QT_PENDING_SPECIFICATION_PREPARATION
							.equals(currentStatus)
					|| (QTStatusConst.QT_CANCELLED.equals(currentStatus) && null == selectedScQt
							.getQtType())) {
				this.getManagedBean().setEditable(
						getEnableFlagByStatus(currentStatus, null, null));
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

				REDIRECT_PAGE = QTConstants.BO_MAINTANANCE_PAGE;
			} else {
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION
						.equals(currentStatus)) {
					this.getManagedBean().setEditable(true);
				}
				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (selectedScQt.isZonal()) {
						if (isNotNullOREmpty(resDTO.getZoneDetails())) {
							for (TplSpecZoneDtl zone : resDTO.getZoneDetails()) {
								getManagedBean()
										.getZoneGeneralTrees()
										.add(LocationSingleton
												.getInstance()
												.constuctTreeNode(
														zone,
														zone.getTplSpecZoneLocs()));
							}
						}
					}
				}
				this.getManagedBean().setGoList(resDTO.getGoList());

				if (QTStatusConst.QT_PENDING_SPECIFICATION_PREPARATION
						.equals(currentStatus)) {

				}

			}
		} finally {
			detailSO = null;
		}
		return REDIRECT_PAGE;
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
		if (!scQt.isSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}
		if (null != scQt.getFinalizationCommitte()
				&& scQt.getFinalizationCommitte().getParameterId() == null) {
			scQt.setFinalizationCommitte(null);
		}
		if (null != scQt.getFinalizationSecratery()
				&& scQt.getFinalizationSecratery().getUserId() == null) {
			scQt.setFinalizationSecratery(null);
		}

		scQt.setExistingContractNo(this.getManagedBean().getCtContract()
				.getContractNo());

		// No need to set the below fields automatically set by listener
		// PmUser users = new FacesScopes().getUserSessionBean();
		/*
		 * scQt.setCreatedBy(users.getUserId());
		 * scQt.setChangedBy(users.getUserId());
		 */
		// Setting the pm status for qt details
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(1l);
		// Setting the Committee Appointment details to Qtdetails
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommitteList = null;
		if (null != committeTabs) {
			qtCommitteList = new ArrayList<ScQtCommittee>();
			for (int i = 0; i < committeTabs.size(); i++) {
				TabBean tabBean = committeTabs.get(i);
				if (!QTConstants.FINALIZATION_SECRETARY_TAB
						.equalsIgnoreCase(tabBean.getTitleName())) {

					if (!"BO".equals(tabBean.getScQtCommittee()
							.getScCommittee().getCommitteeType()
							.getParameterCode())) {
						if (!tabBean.getScQtCommittee().getScCommittee()
								.getScCommitteeMembers().isEmpty()) {
							if (!QTStatusConst.CMT_COMPLETED.equals(tabBean
									.getScQtCommittee().getScCommittee()
									.getCommitteStatus().getStatusId())
									&& !this.getManagedBean().isSaveFlow()) {
								PmStatus committeStatus = new PmStatus();
								committeStatus
										.setStatusId(QTStatusConst.CMT_PENDING_APPROVAL);
								tabBean.getScQtCommittee().getScCommittee()
										.setCommitteStatus(committeStatus);
							}
						} else {
							tabBean.getScQtCommittee().getScCommittee()
									.setApprovalReq(false);
						}
					}

					qtCommitteList.add(tabBean.getScQtCommittee());
				}
			}
		}
		scQt.setScQtCommittees(qtCommitteList);
		// Code for to update the supplier qualification criteria
		List<ScQtCategory> categoryList = new ArrayList<ScQtCategory>();
		if (!this.getManagedBean().getQt().isPanel()) {
			if (this.getManagedBean().getQt().getQtType()
					.equals(QTConstants.OPEN_TYPE)) {
				// TODO need to check why updating the twice.. sagar
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (openList != null && !openList.isEmpty()) {
					for (ScQtCategory scQtCategory : openList) {
						if (null != scQtCategory.getPmCategoryL3()) {
							scQtCategory.setPmCategoryL1(scQtCategory
									.getPmCategoryL3().getPmCategoryL2()
									.getPmCategoryL1());
							scQtCategory.setPmCategoryL2(scQtCategory
									.getPmCategoryL3().getPmCategoryL2());
							// scQtCategory.setScQt(this.getManagedBean().getQt());
							setUserDetails(scQtCategory);
							categoryList.add(scQtCategory);
						}
					}
					scQt.setScQtCategorys(categoryList);
				}
			} else if (this.getManagedBean().getQt().getQtType()
					.equals(QTConstants.RESTRICTED_TYPE)) {
				List<ScQtCategory> restrictedList = this.getManagedBean()
						.getSuppCriteriarestrictedList();
				if (restrictedList != null && !restrictedList.isEmpty()) {
					for (ScQtCategory scQtCategory : restrictedList) {
						scQtCategory.setPmCategoryL1(scQtCategory
								.getPmCategoryL3().getPmCategoryL2()
								.getPmCategoryL1());
						scQtCategory.setPmCategoryL2(scQtCategory
								.getPmCategoryL3().getPmCategoryL2());
						// scQtCategory.setScQt(this.getManagedBean().getQt());
						setUserDetails(scQtCategory);
						categoryList.add(scQtCategory);
					}
					scQt.setScQtCategorys(categoryList);
				}
				List<ScQtSupplier> qtSuppliers = this.getManagedBean()
						.getQtSupplierList();
				if (qtSuppliers != null && !qtSuppliers.isEmpty()) {
					scQt.setScQtSuppliers(qtSuppliers);
				}

			}
		}
		if (!scQt.getScQtSupplierCriteria().isAllState()) {
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), null);
			if (null != locCriteria && !locCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
			} else {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
			}
		}
		/*if (!scQt.isZonal() && !scQt.isPanel()) {
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), null);
			if (null != locCriteria && !locCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
			} else {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
			}
			List<ScQtLocCriteria> existingcheckLocCriteria = (List<ScQtLocCriteria>) checkExistingLocation(
					locCriteria, this.getManagedBean().getExistingSupplierLocationList(), this.getManagedBean().getLocalLevelCode());
			if (existingcheckLocCriteria != null
					&& !existingcheckLocCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(
						existingcheckLocCriteria);
			}
			if (locCriteria != null && !locCriteria.isEmpty()) {
				this.getManagedBean().getSupplierCriteria()
						.setScQtLocCriteria(locCriteria);
				ScQtSupplierCriteria qtSupplierCriteria = this.getManagedBean()
						.getSupplierCriteria();
				if (null != qtSupplierCriteria
						&& null != qtSupplierCriteria.getLocationLevel()
						&& null == qtSupplierCriteria.getLocationLevel()
								.getParameterId()) {
					qtSupplierCriteria.setLocationLevel(null);
				}
				qtSupplierCriteria.setQtZoneCriterias(null);
			}
		} else if (scQt.isZonal() && !scQt.isPanel()) {
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), null);
			List<ScQtLocCriteria> existingcheckLocCriteria = (List<ScQtLocCriteria>) checkExistingLocation(
					locCriteria, this.getManagedBean().getExistingSupplierLocationList(), this.getManagedBean().getLocalLevelCode());
			if (existingcheckLocCriteria != null
					&& !existingcheckLocCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(
						existingcheckLocCriteria);
			}
		}*/
		if (null != this.getManagedBean().getSupplierCriteria()) {
			setUserDetails(this.getManagedBean().getSupplierCriteria());
			scQt.setScQtSupplierCriteria(this.getManagedBean()
					.getSupplierCriteria());
		}
		// Setting the Restricted Supplier List to Qtdetails
		List<ScQtSupplier> qtSupplierList = this.getManagedBean()
				.getQtSupplierList();
		if (qtSupplierList != null && !qtSupplierList.isEmpty()) {
			for (ScQtSupplier scQtSupplier : qtSupplierList) {
				setUserDetails(scQtSupplier);
				// qtSupplierList.add(scQtSupplier);
			}
			scQt.setScQtSuppliers(qtSupplierList);
		}
		// Setting the Approval details to Qtdetails
		List<TabBean> tabList = this.getManagedBean().getTabList();
		List<ScQtApproval> approvalList = null;
		if (tabList != null && !tabList.isEmpty()) {
			approvalList = new ArrayList<ScQtApproval>();
			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(
							tabBean.getBudgetApprovalUsers().getTarget());
				}
				/*if (tabBean.getScQtApproval().getApprovalType().equals(QTConstants.OTHERS_APPROVAL)) {
					for (ScQtApprovalRequest approvalRequest : tabBean.getScQtApproval().getScQtApprovalDtls()) {
						if (approvalRequest.getApproverAction() != null && approvalRequest.getApproverAction().getParameterId() == null) {
							approvalRequest.setApproverAction(null);
						}
						if (approvalRequest.getAppRequestRemark() != null) {
							ScQtAppRequestRemark remarks = approvalRequest.getAppRequestRemark();
							if (remarks.getRemark() == null || remarks.getRemark().equals("")) {
								approvalRequest.setAppRequestRemark(null);
							}
						}
					}
				}*/
				tabBean.getScQtApproval().setScQtApprovalRemarks(
						finalizeRemarks(tabBean.getScQtApproval()
								.getScQtApprovalRemarks()));
				// setUserDetails(tabBean.getScQtApproval());

				approvalList.add(tabBean.getScQtApproval());

			}
		}
		scQt.setScQtApprovals(approvalList);
		scQt.setScQtBriefings(null);
		if (scQt.getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) {
			// Setting the qtBreifing details to Qtdetails
			List<BriefingZoneBean> beansList = this.getManagedBean()
					.getBriefingZoneBean();
			List<ScQtBsv> bsvs = null;
			if (beansList != null && !beansList.isEmpty()) {
				boolean isRequired = false;
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
						bsvs.remove(i);
					}
					if (null != bsvDtls && bsvDtls.size() > 0 && !isRequired) {
						isRequired = true;
					}
				}
				if (bsvs.size() > 0) {
					scQt.setBsvRequired(isRequired);
					scQt.setLatestBsvDate(getLatestBsvDate(bsvs.get(0)
							.getScQtBsvDtls()));
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
					scQt.setBsvRequired(scQtBsv.getScQtBsvDtls().size() > 0 ? true
							: false);
					scQt.setLatestBsvDate(getLatestBsvDate(bsvs.get(0)
							.getScQtBsvDtls()));
				}

			}

			scQt.setScQtBriefings(bsvs);

			// Publish/Closing/sample Date
			Date publishDate = scQt.getPublishDate();
			if (publishDate != null) {
				scQt.setPublishDate(DateUtils.setDefaultDateTime(publishDate));
			}

			Date closingDate = scQt.getClosingDate();
			if (closingDate != null) {
				scQt.setClosingDate(DateUtils.setDefaultDateTime(closingDate));
			}

			Date sampleClosingDate = scQt.getSampleClosingDate();
			if (sampleClosingDate != null) {
				scQt.setSampleClosingDate(DateUtils
						.setDefaultDateTime(sampleClosingDate));
			}

			// Setting Sample Submission to qtDetails.
			List<ScQtSampleSubmission> sampleSubmissionList = null;
			if (scQt.isZonal()) {
				sampleSubmissionList = getManagedBean()
						.getScQtSampleSubmissionList();
				if (sampleSubmissionList != null
						&& !sampleSubmissionList.isEmpty()) {
					for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
						PmAddress pmAddress = scQtSampleSubmission.getAddress();
						if (null != pmAddress) { 
							if (pmAddress.getPmDivision() != null
									&& pmAddress.getPmDivision().getDivisionId() == null) {
								scQtSampleSubmission.getAddress().setPmDivision(
										null);
							}
							scQtSampleSubmission.getAddress().setPmCountry(
									retrieveCountry());
						}
						scQtSampleSubmission.setScQt(getManagedBean().getQt());
					}
				}
			} else {
				sampleSubmissionList = new ArrayList<ScQtSampleSubmission>();
				if (null != this.getManagedBean().getScQtSampleSubmission()) {
					PmAddress pmAddress = this.getManagedBean()
							.getScQtSampleSubmission().getAddress();
					if (pmAddress.getPmDivision() != null
							&& pmAddress.getPmDivision().getDivisionId() == null) {
						this.getManagedBean().getScQtSampleSubmission()
								.getAddress().setPmDivision(null);
					}
					this.getManagedBean().getScQtSampleSubmission()
							.getAddress().setPmCountry(retrieveCountry());
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

			// Setting the Deductions to qtdetails.
			List<ScQtDeduction> deductionList = this.getManagedBean()
					.getScQtDeductionList();
			if (null != deductionList && !deductionList.isEmpty()) {
				for (ScQtDeduction deduction : deductionList) {
					deduction.setScQt(this.getManagedBean().getQt());
					if (null == deduction.getDeductionType().getParameterCode()) {
						throw new EpException(MessageConst.ERROR_100022,
								new String[] { "Deduction Type" });
						/*
						 * } else { List<ScQtDeductionClause> deductionClauses =
						 * deduction .getScQtDeductionClauses(); if (null !=
						 * deductionClauses && !deductionClauses.isEmpty()) {
						 * for (ScQtDeductionClause deductionClause :
						 * deductionClauses) {
						 * deductionClause.setScQtDeduction(deduction); }
						 * deduction.setScQtDeductionClauses(deductionClauses);
						 * }
						 */
					}
				}
			}
			scQt.setScQtDeductions(deductionList);

			// Setting the Documents to qtdetails.
			List<ScQtDocReport> docReportsList = getManagedBean()
					.getScQtDocReportList();
			if (docReportsList != null && !docReportsList.isEmpty()) {
				scQt.setScQtDocReports(docReportsList);
			}
			// Setting the Publication Approvals to QtDetails
			ScQtApproval pubApproval = getManagedBean()
					.getPublicationApproval();
			if (null != pubApproval) {
				if (null == scQt.getScQtApprovals()) {
					approvalList = new ArrayList<ScQtApproval>();
				}

				pubApproval.setApproverSettings(this.getManagedBean()
						.getPublicationApprovals().getTarget());
				List<ScQtApprovalRemark> remarks = pubApproval
						.getScQtApprovalRemarks();
				finalizeRemarks(remarks);
				approvalList.add(pubApproval);
				scQt.setScQtApprovals(approvalList);
			}

		}
		scQt.setScQtStatus(null);
		// save.
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);
		quationTenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);

		quationTenderReqDTO.setToBeRemoveList(this.getManagedBean()
				.getToBeRemoveList());

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
		
		List<ScQtLocCriteria> qtLocCriteriaRemoveList = this.getManagedBean().getSupplierLocationList();
		if (null != qtLocCriteriaRemoveList && !qtLocCriteriaRemoveList.isEmpty()) {
			for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
				criteria.setDeleted(true);
			}
		}
		quationTenderReqDTO.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);
		
		quationTenderReqDTO.setSaveFlow(this.getManagedBean().isSaveFlow());
		return quationTenderReqDTO;
	}

	/**
	 * @return
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

	// Check method for supplier Locations existing
	private List<ScQtLocCriteria> checkExistingLocation(
			List<ScQtLocCriteria> currentLocationList,
			List<ScQtLocCriteria> existingLocations, String locationLevelCode) {
		List<ScQtLocCriteria> criteriaList = new ArrayList<ScQtLocCriteria>();
		if (null == existingLocations || existingLocations.isEmpty()) {
			if (null != currentLocationList && !currentLocationList.isEmpty())
				criteriaList.addAll(currentLocationList);
		} else {
			if (null != currentLocationList) {
				List<PmState> states = new ArrayList<PmState>();
				List<PmDivision> divisions = new ArrayList<PmDivision>();
				List<PmDistrict> districts = new ArrayList<PmDistrict>();
				for (ScQtLocCriteria specLoc : currentLocationList) {
					if (null != locationLevelCode
							&& ParameterConst.LOCATION_LEVEL_STATE
									.equalsIgnoreCase(locationLevelCode)) {
						PmState state = specLoc.getPmState();
						if (null != state) {
							if (states.isEmpty()) {
								for (ScQtLocCriteria existingLoc : existingLocations) {
									states.add(existingLoc.getPmState());
									criteriaList.add(existingLoc);
								}
							}
							if (!states.contains(state)) {
								criteriaList.add(specLoc);
							}
						}
					} else if (null != locationLevelCode
							&& ParameterConst.LOCATION_LEVEL_DISTRICT
							.equalsIgnoreCase(locationLevelCode)) {
						PmDistrict district = specLoc.getPmDistrict();
						if (null != district) {
							if (districts.isEmpty()) {
								for (ScQtLocCriteria existingLoc : existingLocations) {
									districts.add(existingLoc.getPmDistrict());
									criteriaList.add(existingLoc);
								}
							}
							if (!districts.contains(district)) {
								criteriaList.add(specLoc);
							}
						}
					} else if (null != locationLevelCode
							&& ParameterConst.LOCATION_LEVEL_DIVISION
							.equalsIgnoreCase(locationLevelCode)) {
						PmDivision divison = specLoc.getPmDivision();
						if (null != divison) {
							if (divisions.isEmpty()) {
								for (ScQtLocCriteria existingLoc : existingLocations) {
									divisions.add(existingLoc.getPmDivision());
									criteriaList.add(existingLoc);
								}
							}
							if (!divisions.contains(divison)) {
								criteriaList.add(specLoc);
							}
						}
					}
				}
			}
		}
		return criteriaList;
	}

	/**
	 * @return
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
	private void populateQTSearchCriteria(
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (qtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
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
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
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

	public void retriveFinancialSpecification() {
		debugLogger("retriveFinancialSpecification");
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
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

	public void retriveTechnicalSpecification() {
		debugLogger("retriveTechnicalSpecification");
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType(QTConstants.TECHNICAL_SPEC_TYPE);
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

	public void retrieveBriefingSitevisitTabDtl() {
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
				reqDTO.setScQt(this.getManagedBean().getQt());
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

	public void processChecked(AjaxBehaviorEvent e) {
		// Boolean id = (Boolean) e.getComponent().getAttributes().get("value");

	}

	public void retrieveCommitteeAppointmentTabDtl() {
		debugLogger("retrieveCommitteeAppointmentTabDtl");
		ScQt scQt = this.getManagedBean().getQt();
		Long currentStatus = scQt.getQtStatus().getStatusId();
		this.getManagedBean().setCurrentStatusId(currentStatus);
		if (null == this.getManagedBean().getQt().getFinalizationCommitte()) {
			this.getManagedBean().getQt()
					.setFinalizationCommitte(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getFinalizationSecratery()) {
			this.getManagedBean().getQt()
					.setFinalizationSecratery(new PmUser());
		}
		MenuModel menuModel = new DefaultMenuModel();
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		Long qtId = scQt.getQtId();
		if (null != qtId && null == committeTabs) {
			String evaluationType = this.getManagedBean().getQt()
					.getEvaluationType();
			committeTabs = new ArrayList<TabBean>();
			// BO and specification committe is common for both evaluations..
			ScQtCommittee boCommitte = getQtCommiteDtlsByQtIdNType("BO", qtId);
			committeTabs
					.add(new TabBean(QTConstants.BUSINESS_OWNER, boCommitte));

			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(
					CommitteType.getValue("SpecificationCommittee"), qtId);
			if (QTStatusConst.CMT_COMPLETED.equals(specCommitte
					.getScCommittee().getCommitteStatus().getStatusId())) {
				this.getManagedBean().setCloneNeeded(true);
			}
			copyBoMembersToSpecCommitte(specCommitte, boCommitte);
			boolean isEnable = false;
			if (QTStatusConst.CMT_PENDING_REVISION.equals(specCommitte
					.getScCommittee().getCommitteStatus().getStatusId())) {
				isEnable = false;
			} else if (currentStatus >= QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) {
				isEnable = true;
			}
			committeTabs.add(new TabBean(
					QTConstants.SPECIFICATION_COMMITTEE_TAB, specCommitte,
					isEnable));
			// evaluation type is one tier only oc is added..
			if (QTConstants.EVALUATION_ONE_TIER.equals(evaluationType)) {
				boolean ecEditable = false;
				ScQtCommittee evalCommitte = getQtCommiteDtlsByQtIdNType(
						CommitteType.getValue("EvaluationCommittee"), qtId);
				if (this.getManagedBean().isEditable()) {
					ecEditable = getEnableFlagByStatus(currentStatus,
							scQt.getEvaluationType(),
							evalCommitte.getScCommittee());
				}
				committeTabs.add(new TabBean(
						QTConstants.EVALUATION_COMMITTEE_TAB, evalCommitte,
						ecEditable));
			}
			// evaluation type is two tier below committe are added
			else {
				boolean ocEditable = false;
				ScQtCommittee openingCommitte = getQtCommiteDtlsByQtIdNType(
						CommitteType.getValue("OpeningCommittee"), qtId);
				if (this.getManagedBean().isEditable()) {
					ocEditable = getEnableFlagByStatus(currentStatus,
							scQt.getEvaluationType(),
							openingCommitte.getScCommittee());
				}
				committeTabs.add(new TabBean(QTConstants.OPENING_COMMITTEE_TAB,
						openingCommitte, ocEditable));
				boolean tecEditable = false;
				ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(
						CommitteType
								.getValue(ParameterConst.DOCTYPE_TEC_COMMITTE),
						qtId);
				if (this.getManagedBean().isEditable()) {
					tecEditable = getEnableFlagByStatus(currentStatus,
							scQt.getEvaluationType(),
							tecCommitte.getScCommittee());
				}
				committeTabs.add(new TabBean(
						ParameterConst.DOCTYPE_TEC_COMMITTE, tecCommitte,
						tecEditable));
				ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(
						CommitteType
								.getValue(ParameterConst.DOCTYPE_FEC_COMMITTE),
						qtId);
				boolean fecEditable = false;
				if (this.getManagedBean().isEditable()) {
					fecEditable = getEnableFlagByStatus(currentStatus,
							scQt.getEvaluationType(),
							fecCommitte.getScCommittee());
				}

				committeTabs.add(new TabBean(
						ParameterConst.DOCTYPE_FEC_COMMITTE, fecCommitte,
						fecEditable));
			}
			//
			List<PmParameter> finalizeList = retrieveFinalizationSecratryDtl(qtId);
			this.getManagedBean().setFinalizationSecratary(finalizeList);
			// Displaying the Secretary Details
			/*
			 * List<PmUser> pmUsersList = retrievePmUserSecratryDtl(commType);
			 * this.getManagedBean().setPmUsersSecretaryList(pmUsersList);
			 */

			ScCommittee committee = null;
			if (this.getManagedBean().isEditable()) {
				isEnable = getEnableFlagByStatus(currentStatus,
						scQt.getEvaluationType(), committee);
			}
			committeTabs
					.add(new TabBean(QTConstants.FINALIZATION_SECRETARY_TAB,
							committee, isEnable));

			this.getManagedBean().setScCommitteeMemberTabList(committeTabs);

			int zoneTabIndex = 0;
			for (TabBean tabBean : committeTabs) {
				MenuItem committeItem = new MenuItem();
				committeItem.setValue(tabBean.getTitleName());
				committeItem.setUpdate("@this");
				committeItem.setPartialSubmit(true);
				committeItem.setProcess("@this");
				if (!QTConstants.FINALIZATION_SECRETARY_TAB
						.equalsIgnoreCase(tabBean.getTitleName())) {
					if (tabBean.getScQtCommittee().getScCommittee()
							.getCommitteStatus().getStatusId()
							.equals(QTStatusConst.CMT_COMPLETED)) {
						committeItem.setIcon("ui-icon-endorse");
					}
				}
				committeItem.setOnclick("committeTabChangeAction([ {name : 'selectedCommitteeTabIndex', value : " + zoneTabIndex + "}]);");
				menuModel.addMenuItem(committeItem);
				zoneTabIndex++;
			}
			this.getManagedBean().setMenuModel(menuModel);
			if (committeTabs.size() > 0) {
				// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
				this.getManagedBean().setSelectedTabBean(
						committeTabs.get(this.getManagedBean()
								.getSelectedTabIndex()));
			}
		}
	}

	/**
	 * @param specCommitte
	 * @param boCommitte
	 */
	private void copyBoMembersToSpecCommitte(ScQtCommittee specCommitte,
			ScQtCommittee boCommitte) {
		List<ScCommitteeMember> spceMembers = specCommitte.getScCommittee()
				.getScCommitteeMembers();
		List<ScCommitteeMember> boMembers = boCommitte.getScCommittee()
				.getScCommitteeMembers();
		if (QTStatusConst.CMT_PENDING_SUBMISSION.equals(specCommitte
				.getScCommittee().getCommitteStatus().getStatusId())
				&& spceMembers.isEmpty()) {

			for (ScCommitteeMember committeeMember : boMembers) {

				try {
					ScCommitteeMember specCommitteeMember = (ScCommitteeMember) BeanUtils
							.cloneBean(committeeMember);
					specCommitteeMember.setCommitteeMemberId(null);
					specCommitteeMember.setChangedDate(null);
					specCommitteeMember.setScCommittee(specCommitte
							.getScCommittee());
					specCommitteeMember.setScCommittee(specCommitte
							.getScCommittee());
					spceMembers.add(specCommitteeMember);

				} catch (IllegalAccessException e) {
					throw new EpException(
							"Error while copying the members into spec");

				} catch (InvocationTargetException e) {
					throw new EpException(
							"Error while copying the members into spec");
				} catch (InstantiationException e) {
					throw new EpException(
							"Error while copying the members into spec");
				} catch (NoSuchMethodException e) {
					throw new EpException(
							"Error while copying the members into spec");
				}
			}
		}

	}

	/**
	 * @param qtId
	 * @return
	 */
	private List<PmParameter> retrieveFinalizationSecratryDtl(Long qtId) {
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(qtId);
		CommitteResDTO resDTO = null;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveFinalizationSecratryDtls(reqDTO);
		} finally {
			committeSO = null;
		}
		return resDTO.getFinalizationSecratry();
	}

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

	public void preRenderView() {
		Long qtId = null;
		Object payLoad = getBpmTaskDetails(Object.class);
		if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(this
				.getManagedBean().getTaskName())
				|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean().getTaskName())
				|| SCProcessNameConst.ASSIGN_FC_SECRETARY.equalsIgnoreCase(this
						.getManagedBean().getTaskName())) {
			if (!SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getTaskName())
					&& !SCProcessNameConst.ASSIGN_FC_SECRETARY
							.equalsIgnoreCase(this.getManagedBean()
									.getTaskName())) {

				SCCommitteeDataType committeDataType = (SCCommitteeDataType) payLoad;
				this.getManagedBean().setQuotationTenderDataType(
						committeDataType);

				qtId = committeDataType.getQtId().longValue();
			} else {

				SCEvaluationDataType evalDataType = (SCEvaluationDataType) payLoad;
				this.getManagedBean().setQuotationTenderDataType(evalDataType);
				qtId = evalDataType.getQtId().longValue();

			}
			this.getManagedBean().setActiveIndex(1);
			retrieveFinalizationSecratryDtl(qtId);
			retrieveQuationTenderDetails(new Long(qtId), null);
			retrieveCommitteeAppointmentTabDtl();
		} else {
			// qtId = quotationTenderDataType.getQtId().longValue();
			SCQuotationTenderDataType quationDataType = (SCQuotationTenderDataType) payLoad;
			qtId = quationDataType.getQtId().longValue();
			this.getManagedBean().setQuotationTenderDataType(quationDataType);
			// retrieveFinalizationSecratryDtl(qtId);
			retrieveQuationTenderDetails(new Long(qtId), null);
		}

		if (null == this.getManagedBean().getQt().getProcurementMode()) {
			this.getManagedBean().getQt().setProcurementMode(new PmParameter());
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);
	}

	public String updateBpmTask(String outcome) {

		QuationTenderReqDTO qtReqDTO = prepareQTInfo();

		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctxToken);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		String taskName = res.getTaskName();

		qtReqDTO.setOutcome(outcome);
		IQTDetailSO detailSO;
		try {
			ScQt qt = qtReqDTO.getQtdetail();
			// Added by visu on 09012015 - Fix for Other Approval Validations on
			// sumbit only
			qt.setOtherApproval(true);

			Long currentStatus = qt.getQtStatus().getStatusId();
			if (currentStatus
					.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)) {
				qt.setModified(true);
				// check if its Pending Schedule and Documents , only execute
				// this part
				if (null == this.getManagedBean().getScQtDocReportList()) {
					retrieveDocumentTabDtl();
					qt.setScQtDocReports(this.getManagedBean()
							.getScQtDocReportList());
				}
			}
			CommitteResDTO committeResDto = null;
			QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(res.getPojo());
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
			} else if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
					.equalsIgnoreCase(taskName)) {

				// TODO - temporary for FC.
				if (res.getPojo() instanceof SCEvaluationDataType) {
					if ("FC".equals(((SCEvaluationDataType) res.getPojo())
							.getCommitteeType())) {
						ICommitteSO committeSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										ICommitteSO.class);
						CommitteReqDTO committeReqDTO = new CommitteReqDTO();
						committeReqDTO.setToken(ctxToken);
						committeReqDTO.setTaskId(this.getManagedBean()
								.getTaskId());
						committeReqDTO.setPayLoad(res.getPojo());
						committeReqDTO.setOutcome(outcome);
						committeReqDTO.setScQt(qt);
						committeResDto = committeSO
								.updateFinalizateSecrataryTask(committeReqDTO);
					} else {
						ICommitteSO committeSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										ICommitteSO.class);
						CommitteReqDTO committeReqDTO = new CommitteReqDTO();
						committeReqDTO.setToken(ctxToken);
						committeReqDTO.setTaskId(this.getManagedBean()
								.getTaskId());
						committeReqDTO.setPayLoad(res.getPojo());
						committeReqDTO.setOutcome(outcome);
						committeReqDTO.setScQt(qt);
						committeResDto = committeSO
								.updateBPMAction(committeReqDTO);
					}
				}

			} else if (SCProcessNameConst.ASSIGN_FC_SECRETARY
					.equalsIgnoreCase(taskName)) {
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(res.getPojo());
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO
						.updateFinalizateSecrataryTask(committeReqDTO);

			} else {
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setPayLoad(res.getPojo());
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				FacesScopes facesScope = new FacesScopes();
				qtReqDTO.setPmUser(facesScope.getUserSessionBean());
				qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
				qtReqDTO.setRoleCode(getUserRoleCode());
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

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
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	public void setOutComeAction(String outCome) {
		this.getManagedBean().setOutCome(outCome);

	}

	public void retrieveApprovalTabDtl() {
		debugLogger("retrieveApprovalTabDtl");
		// Added by Fara on 03092014 - Fix for Approval tab if user dint visit
		// the Committee Appointment tab
		List<TabBean> tabList = this.getManagedBean()
				.getScCommitteeMemberTabList();
		if (tabList == null || tabList.size() == 0) {
			retrieveCommitteeAppointmentTabDtl();
		}

		updateQtDtls();
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(this.getManagedBean().getQt());

			QuationTenderResDTO resDTO = detailSO
					.retriveQTApprovalDetails(reqDTO);
			List<ScQtApproval> requiredApproval = resDTO.getApproval();
			List<TabBean> tabBeans = new ArrayList<TabBean>();
			List<TabBean> existingApprovalTabs = this.getManagedBean()
					.getTabList();

			/*
			 * this.getManagedBean().setToBeRemoveList(
			 * getToBeRemoveApprovals(requiredApproval));
			 */

			MenuModel menuModel = new DefaultMenuModel();
			if (requiredApproval != null) {
				for (ScQtApproval qtApproval : requiredApproval) {
					qtApproval = checkIsApprovalExists(qtApproval,
							existingApprovalTabs);

					if (!qtApproval.isDeleted()) {
						qtApproval.setApprovalType(ApprovalCategory
								.getDisplayName(qtApproval
										.getApprovalCategory()
										.getParameterCode()));

						qtApproval.setScQtApprovalRemarks(initializeRemarks(
								qtApproval, this.getManagedBean().getQt()
										.getCurrentWorkFlowStatus(),
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
						if (qtApproval.getApprovalCategory().getParameterCode()
								.equals(ParameterConst.APPROVAL_CATEGORY_BPK)) {
							tabBeans.add(new TabBean(qtApproval
									.getApprovalType(), qtApproval, settings));
						} else if (qtApproval.getApprovalCategory()
								.getParameterCode()
								.equals(ParameterConst.APPROVAL_CATEGORY_PTJ)) {
							List<ScQtApprovalRequest> approvarRequests = qtApproval
									.getScQtApprovalDtls();
							if (null != approvarRequests
									&& !approvarRequests.isEmpty()) {
								for (ScQtApprovalRequest scQtApprovalRequest : approvarRequests) {
									if (null != scQtApprovalRequest
											.getApproverAction()
											&& SourcingConstants.APPROVE_ACTION
													.equalsIgnoreCase(scQtApprovalRequest
															.getApproverAction()
															.getParameterCode())) {
										DualListModel<ScQtApproverSettings> ptjApprovals = approverSettingDtls(
												RoleCode.PUBLICATION_APPROVER,
												qtApproval);
										tabBeans.add(new TabBean(qtApproval
												.getApprovalType(), qtApproval,
												ptjApprovals));
										break;
									}
								}
							}
						} else if (qtApproval
								.getApprovalCategory()
								.getParameterCode()
								.equals(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE)) {
							List<ScQtApproverSettings> approvalSettings = qtApproval
									.getApproverSettings();
							if (null == approvalSettings
									|| approvalSettings.isEmpty()) {
								approvalSettings = new ArrayList<ScQtApproverSettings>();
								ScQtApproverSettings approverSettings = new ScQtApproverSettings();
								approverSettings.setApprover(new PmUser());
								approverSettings.setQtApproval(qtApproval);
								approvalSettings.add(approverSettings);
								qtApproval
										.setApproverSettings(approvalSettings);
							} else {
								for (ScQtApproverSettings approverSettings : approvalSettings) {
									approverSettings.setQtApproval(qtApproval);
								}
							}
							tabBeans.add(new TabBean(
									qtApproval.getApprovalType(),
									qtApproval,
									pbaApproverSettingDtls(qtApproval, this
											.getManagedBean().getQt().getQtId())));
						} else if (qtApproval
								.getApprovalCategory()
								.getParameterCode()
								.equals(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT)) {
							tabBeans.add(new TabBean(qtApproval
									.getApprovalType(), qtApproval,
									approverSettingDtls(
											RoleCode.COMMITTEE_APPROVER,
											qtApproval)));
						} else if (qtApproval
								.getApprovalCategory()
								.getParameterCode()
								.equals(ParameterConst.APPROVAL_CATEGORY_OTHERS)) {
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
									}
								}
							}
							tabBeans.add(new TabBean(qtApproval
									.getApprovalType(), qtApproval, settings));
						}
					} else {
						if (this.getManagedBean().getToBeRemoveList() == null) {
							this.getManagedBean().setToBeRemoveList(
									new ArrayList<ScQtApproval>());
							this.getManagedBean().getToBeRemoveList()
									.add(qtApproval);
						} else {
							this.getManagedBean().getToBeRemoveList()
									.add(qtApproval);
						}
					}
				}
			}
			int approvalTabIndex = 0;
			for (TabBean approvalTabBean : tabBeans) {
				MenuItem approvalItem = new MenuItem();
				approvalItem.setValue(approvalTabBean.getTitleName());
				approvalItem.setUpdate("@this");
				approvalItem.setPartialSubmit(true);
				approvalItem.setProcess("@this");
				approvalItem.setOnclick("approvalTabChangeAction([ {name : 'selectedApprovalTabIndex', value : " + approvalTabIndex + "}]);");
				menuModel.addMenuItem(approvalItem);
				approvalTabIndex++;
			}
			this.getManagedBean().setApprovalMenuModel(menuModel);

			// Added by Fara on 03092014 - Fix for Approval tab if no data for
			// approval
			if (menuModel.getContents() != null
					&& menuModel.getContents().size() > 0) {
				/*
				 * this.getManagedBean().setSelectedApprovalTabBean(
				 * tabBeans.get(0));
				 */
				this.getManagedBean().setSelectedApprovalTabBean(
						tabBeans.get(this.getManagedBean()
								.getSelectedApprovalTabIndex()));
				this.getManagedBean().setTabList(tabBeans);
			}
		} finally {
			detailSO = null;
		}
	}

	public void retrieveSuppQualificationTabDtl() {
		debugLogger("retrieveSuppQualificationTabDtl");
		if (this.getManagedBean().getQt().isZonal() && this.getManagedBean().getQt().isPanel()) {
			retrieveQuationTenderDetails(this.getManagedBean().getQt().getQtId(), QTConstants.DOCTYPE_QT);
		}
		IQTDetailSO QtDetailsSO;
		QuationTenderResDTO tenderResDTO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());

			if (!this.getManagedBean().getQt().isPanel()) {
				if ((this.getManagedBean().getQt().getQtType().equals(QTConstants.OPEN_TYPE) && (null == this
						.getManagedBean().getQtCategoryList() || this
						.getManagedBean().getQtCategoryList().isEmpty()))
						|| (this.getManagedBean().getQt().getQtType()
								.equals(QTConstants.RESTRICTED_TYPE) && (null == this.getManagedBean()
								.getSuppCriteriarestrictedList() || this
								.getManagedBean()
								.getSuppCriteriarestrictedList().isEmpty()))) {
					tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
					List<ScQtCategory> categoryResults = tenderResDTO.getCategoryResultBean().getResultList();
					if (isNotNullOREmpty(categoryResults)) {
						if (QTConstants.OPEN_TYPE.equalsIgnoreCase(tenderResDTO
								.getQtDetail().getQtType())) {
							this.getManagedBean().setQtCategoryList(
									categoryResults);
						} else if (QTConstants.RESTRICTED_TYPE
								.equalsIgnoreCase(tenderResDTO.getQtDetail()
										.getQtType())) {
							this.getManagedBean()
									.setSuppCriteriarestrictedList(
											categoryResults);
						}
					}
					ScQt scQt = tenderResDTO.getQtDetail();
					this.getManagedBean().setAllStateCode(
							tenderResDTO.getStateCode());
					this.getManagedBean().setQt(scQt);

					if (null == scQt.getScQtSupplierCriteria()) {
						this.getManagedBean().setSupplierCriteria(
								new ScQtSupplierCriteria());
						this.getManagedBean().getSupplierCriteria()
								.setScQt(scQt);
					} else {
						this.getManagedBean().setSupplierCriteria(
								scQt.getScQtSupplierCriteria());
					}

					if (scQt.isPanel()
							|| SourcingConstants.QT_TYPE_RESTRICTED_TENDER
									.equalsIgnoreCase(scQt.getQtType())) {
						if (null != scQt.getScQtSuppliers()) {
							this.getManagedBean().setQtSupplierList(
									scQt.getScQtSuppliers());
							this.getManagedBean().setTotalSupplier(
									scQt.getScQtSuppliers().size());
						} else {
							this.getManagedBean().setQtSupplierList(
									new ArrayList<ScQtSupplier>());
						}
					}

					if (scQt.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND
							&& this.getManagedBean().getAllStateCode() == 3
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
					}

					if (scQt.getEstimatedAmount().intValue() <= SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
						if (null == scQt.getScQtSupplierCriteria()
								.getLocationLevel()) {
							scQt.getScQtSupplierCriteria().setLocationLevel(
									new PmParameter());
						}
						scQt.getScQtSupplierCriteria().setAllState(false);
					}

					if (!scQt.getScQtSupplierCriteria().isAllState()) {
						List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
						getManagedBean().setZoneTrees(null);
						if (null != tenderResDTO.getLocationDetails()) {
							if (null == this.getManagedBean()
									.getTotalSuppliersSpecLoc()) {
								this.getManagedBean().setTotalSuppliersSpecLoc(
										new ArrayList<TplSpecLoc>());
							}
							this.getManagedBean().getTotalSuppliersSpecLoc()
									.addAll(tenderResDTO.getLocationDetails());
							for (TplSpecLoc localLoc : tenderResDTO
									.getLocationDetails()) {
								if (null != scQt.getScQtSupplierCriteria()
										&& null != scQt
												.getScQtSupplierCriteria()
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
								this.getManagedBean().setExistingSupplierLocationList(locCriteria);
								getManagedBean().getZoneTrees().addAll(
										LocationSingleton.getInstance()
												.constuctTreeNode(locCriteria));
							}
						}
					}
				} else {
					ScQt scQt = this.getManagedBean().getQt();
					if (scQt.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND
							&& this.getManagedBean().getAllStateCode() == 3
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
					}
					if (scQt.getEstimatedAmount().intValue() <= SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
						if (null == scQt.getScQtSupplierCriteria()
								.getLocationLevel()) {
							scQt.getScQtSupplierCriteria().setLocationLevel(
									new PmParameter());
						}
						scQt.getScQtSupplierCriteria().setAllState(false);
					}
					this.getManagedBean().setQt(scQt);
				}
			} else {
				tenderResDTO = QtDetailsSO
						.retriveQTSuppQulCriDetails(tenderReqDTO);
				ScQt scQt = tenderResDTO.getQtDetail();
				this.getManagedBean().setAllStateCode(
						tenderResDTO.getStateCode());
				this.getManagedBean().setQt(scQt);
				if (null != scQt.getScQtSupplierCriteria()
						&& null != scQt.getScQtSupplierCriteria()
								.getCtPanelItem()) {
					this.getManagedBean().setCtPanelItem(
							scQt.getScQtSupplierCriteria().getCtPanelItem());
					if (null != scQt.getScQtSupplierCriteria().getCtPanelItem()
							.getCtCircularVer()) {
						this.getManagedBean().setCtCircularVer(
								scQt.getScQtSupplierCriteria().getCtPanelItem()
										.getCtCircularVer());
					}
					this.getManagedBean().setPanelItemCategory(
							getParameterDesc(scQt.getScQtSupplierCriteria()
									.getCtPanelItem().getItemCategoryId()));
					List<CtPanelItem> panelList = this.getManagedBean()
							.getSelectpanelItem();
					panelList = new ArrayList<CtPanelItem>();

					panelList.add(this.getManagedBean().getQt()
							.getScQtSupplierCriteria().getCtPanelItem());
					this.getManagedBean().setSelectpanelItem(panelList);
				}
				if (null != this.getManagedBean().getCtCircularVer()) {
					this.getManagedBean().setCircularExpDate(
							this.getManagedBean().getCtCircularVer()
									.getEndDate());
					this.getManagedBean().setCircularNo(
							this.getManagedBean().getCtCircularVer()
									.getCircularNo());
					this.getManagedBean().setCircularName(
							this.getManagedBean().getCtCircularVer().getName());
					this.getManagedBean().setCircularType(
							this.getManagedBean().getCtCircularVer()
									.getDocType());
				}
				if (null != this.getManagedBean().getZoneGeneralTrees()
						&& this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {

					/*
					 * if (null != tenderResDTO.getZoneDetails() &&
					 * !tenderResDTO.getZoneDetails().isEmpty()) {
					 * this.getManagedBean().setZoneGeneralTrees( new
					 * ArrayList<TreeNode>()); for (TplSpecZoneDtl zoneDtl :
					 * tenderResDTO .getZoneDetails()) { getManagedBean()
					 * .getZoneGeneralTrees() .add(LocationSingleton
					 * .getInstance() .constuctTreeNode( zoneDtl,
					 * zoneDtl.getTplSpecZoneLocs()));
					 * this.getManagedBean().setCtzones(true); }
					 * 
					 * }
					 */
					retriveZonesByCircularNo();

				}
			}
		} finally {

		}
	}

	private void retriveZonesByCircularNo() {
		debugLogger("retriveZonesByCircularNo");
		IPanelSupplierSO panelSupplierSO;
		PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {
			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);
			
			if (null != this.getManagedBean().getCtPanelItem()
					&& null != this.getManagedBean().getCtPanelItem()
							.getPanelItemId()) {

				reqDTO.setPanelItem(this.getManagedBean().getCtPanelItem());
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
							/*
							 * getManagedBean() .getZoneGeneralTrees()
							 * .add(LocationSingleton .getInstance()
							 * .constuctPanelTreeNodes( panelZones, panelZones
							 * .getPanelZoneDtls()));
							 */
						}
					}
				}
			} else {
				if (null != this.getManagedBean().getCtPanelItem()
						&& null != this.getManagedBean().getCtPanelItem()
								.getPanelItemId()) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					PanelSupplierReqDTO supplierreqDTO = new PanelSupplierReqDTO();
					supplierreqDTO.setPanelItemId(this.getManagedBean()
							.getCtPanelItem().getPanelItemId());
					supplierreqDTO.setFetchSubEntity(true);
					PanelSupplierResDTO panelSupplierResDTO = panelSupplierSO
							.retrievePanelItem(supplierreqDTO);
					this.getManagedBean().setTotalSupplier(0);
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != panelSupplierResDTO.getCtPanelItem()) {
						CtPanelItem panelItem = panelSupplierResDTO
								.getCtPanelItem();
						if (null != panelItem.getCtPanelSuppliers()) {
							this.getManagedBean().setTotalSupplier(
									panelItem.getCtPanelSuppliers().size());
							this.getManagedBean().setCtZoneSupplierList(
									panelItem.getCtPanelSuppliers());
						}
					}
				}
			}
			List<CtPanelItem> itemList = this.getManagedBean()
					.getSelectpanelItem();
			itemList = new ArrayList<CtPanelItem>();
			itemList.add(this.getManagedBean().getCtPanelItem());
			this.getManagedBean().setSelectpanelItem(itemList);
		} finally {
		}
	}

	// To get the schedule tab details from so..
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
				this.getManagedBean().setQt(resDTO.getQtDetail());
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
							.setOfficerType(SourcingConstants.CONTACT_PERSON_TYPE);
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
						backingBean.retrievePmStateList();
					}
				}

				backingBean.getDeskOfficerAdminAssistant();
			} finally {
				detailSO = null;
			}
		}
		if (this.getManagedBean().getQt().isSampleReq()) {
			QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
			backingBean.retrievePmStateList();
		}
		Date publishDate = this.getManagedBean().getQt().getPublishDate();
		Date bsvDate = null;
		/*
		 * List<BriefingZoneBean> briefingList = this.getManagedBean()
		 * .getBriefingZoneBean(); if (briefingList == null ||
		 * briefingList.size() == 0) { retrieveBriefingSitevisitTabDtl(); }
		 */
		if (getManagedBean().getQt().isZonal()) {
			List<BriefingZoneBean> briefingBeans = getManagedBean()
					.getBriefingZoneBean();
			if (null != briefingBeans && !briefingBeans.isEmpty()) {
				for (BriefingZoneBean briefing : briefingBeans) {
					briefing.getScQtBsv().getScQtBsvDtls();
					ScQtBsv bsv = briefing.getScQtBsv();
					if (bsv.getScQtBsvDtls() != null) {
						for (ScQtBsvDtl bsvDtl : bsv.getScQtBsvDtls()) {
							bsvDate = bsvDtl.getBsvDate();
							this.getManagedBean().setBsvDate(bsvDate);
							getManagedBean().getQt().setClosingDate(
									DateUtils.addDays(bsvDate, this
											.getManagedBean().getQt()
											.getPublishPeriod()));
						}
					}
				}
			} else {
				if (null != publishDate) {
					getManagedBean().getQt().setClosingDate(
							DateUtils.addDays(getManagedBean().getQt()
									.getPublishDate(), this.getManagedBean()
									.getQt().getPublishPeriod()));
				}
			}
		} else {
			if (getManagedBean().getBsvDtlBean() != null) {
				List<ScQtBsvDtl> selectedBriefList = getManagedBean()
						.getBsvDtlBean().getScQtBsv().getScQtBsvDtls();
				if (selectedBriefList != null && !selectedBriefList.isEmpty()) {
					for (ScQtBsvDtl scQtBsvDtl : selectedBriefList) {
						bsvDate = scQtBsvDtl.getBsvDate();
						this.getManagedBean().setBsvDate(bsvDate);
						getManagedBean().getQt().setClosingDate(
								DateUtils.addDays(bsvDate, this
										.getManagedBean().getQt()
										.getPublishPeriod()));
					}
				} else {
					if (null != publishDate) {
						getManagedBean().getQt().setClosingDate(
								DateUtils.addDays(getManagedBean().getQt()
										.getPublishDate(), this
										.getManagedBean().getQt()
										.getPublishPeriod()));
					}
				}
			} else {
				if (null != publishDate) {
					getManagedBean().getQt().setClosingDate(
							DateUtils.addDays(getManagedBean().getQt()
									.getPublishDate(), this.getManagedBean()
									.getQt().getPublishPeriod()));
				}
			}
		}
		Date clsDate = this.getManagedBean().getQt().getClosingDate();
		if (null != clsDate) {
			this.getManagedBean().setProposalValidityUntil(
					DateUtils.addDays(clsDate, this.getManagedBean().getQt()
							.getQtValidityPeriod()));
		}
	}

	public void retrieveDeductionPaymentTabDtl() {
		debugLogger("retrieveDeductionPaymentTabDtl");
		if (this.getManagedBean().getScQtDeductionList() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);
				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();
				this.getManagedBean().setScQtDeductionList(deductionList);
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

	public void retrieveDocumentTabDtl() {
		debugLogger("retrieveDocumentTabDtl");
		IQTDetailSO detailSO;
		if (getManagedBean().getScQtDocReportList() == null) {

			QuationTenderResDTO resDTO = null;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtdetail(getManagedBean().getQt());
				reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

				resDTO = detailSO.retriveQTDocDetails(reqDTO);
				if (resDTO.getReports() != null
						&& !resDTO.getReports().isEmpty()) {
					this.getManagedBean().setScQtDocReportList(
							resDTO.getReports());
				}

			} finally {
				detailSO = null;
			}
		}
	}

	public void retrieveActivitiesTabDtl() {
		debugLogger("retrieveActivitiesTabDtl");

		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			// reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setQtdetail(this.getManagedBean().getQt());
			QuationTenderResDTO resDTO = detailSO.retrieveQTActivities(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setQtActivityBeans(
						resDTO.getQtActivityBeans());
			}
		} finally {
			detailSO = null;
		}
	}

	public void retrievePublicationApprovalTabDtl() {
		debugLogger("retrievePublicationApprovalTabDtl");
		IQTDetailSO detailSO;
		DualListModel<ScQtApproverSettings> dualListModel = null;
		try {
			if (null == this.getManagedBean().getPublicationApproval()) {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());
				reqDTO.setApprovalType(ParameterConst.APPROVAL_CATEGORY_PTJ);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalDtlsByQtIdNType(reqDTO);
				ScQtApproval publicationApproval = resDTO.getQtApproval();
				if (publicationApproval == null) {
					publicationApproval = new ScQtApproval();
					publicationApproval
							.setScQtApprovalRemarks(initializeRemarks(
									publicationApproval,
									this.getManagedBean().getQt()
											.getCurrentWorkFlowStatus(),
									publicationApproval
											.getScQtApprovalRemarks(),
									ScQtApprovalRemark.class));
					publicationApproval.setScQtApprovalAttachment(null);
					publicationApproval.setScQt(this.getManagedBean().getQt());
					List<ScQtApprovalRequest> approvalDtls = new ArrayList<ScQtApprovalRequest>();
					ScQtApprovalRequest scQtApprovalDtl = new ScQtApprovalRequest();
					ParameterReqDTO reqDTO1 = new ParameterReqDTO();
					reqDTO1.setParameterCode(ParameterConst.APPROVAL_CATEGORY_PTJ);
					reqDTO1.setParameterType(ParameterConst.APPROVAL_CATEGORY_TYPE);
					PmParameter parameter = getParameterSO()
							.retrieveParameterByTypeAndCode(reqDTO1)
							.getPmParameter();
					scQtApprovalDtl.setApprovalType(parameter);
					scQtApprovalDtl.setScQtApproval(publicationApproval);
					approvalDtls.add(scQtApprovalDtl);
					publicationApproval.setScQtApprovalDtls(approvalDtls);
					publicationApproval.setApprovalCategory(parameter);

				} else {
					publicationApproval
							.setScQtApprovalRemarks(initializeRemarks(
									publicationApproval,
									this.getManagedBean().getQt()
											.getCurrentWorkFlowStatus(),
									publicationApproval
											.getScQtApprovalRemarks(),
									ScQtApprovalRemark.class));

				}
				dualListModel = approverSettingDtls(
						RoleCode.PUBLICATION_APPROVER, publicationApproval);
				this.getManagedBean().setPublicationApprovals(dualListModel);
				this.getManagedBean().setPublicationApproval(
						publicationApproval);
				log.info("Remark Size : "
						+ this.getManagedBean().getPublicationApproval()
								.getScQtApprovalRemarks().size());
			}
		} finally {
			detailSO = null;
		}
	}

	private class QTPagingDataModel extends PagingDataModel<ScQt> {
		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean qtnTenderSearchBean;

		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}

		/**
		 * retrieve list of ScQt by params
		 * 
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
				populateQTSearchCriteria(qtnTenderSearchBean, searchBean);
				reqDTO.setQtSearchBean(searchBean);
				QuationTenderResDTO resDTO = detailSO.getQuationTenders(reqDTO);
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}

	public void reset() {
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
	}

	private List<ScQtLocCriteria> getQuoteZones(List<TreeNode> treeNodes,
			ScQtSupplierCriteria supplierCriteria) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				List<TreeNode> stateNodes = treeNode.getChildren();
				for (TreeNode treeNode2 : stateNodes) {
					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {
							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {
									ScQtLocCriteria criteria = ((ScQtLocCriteria) districtNode
											.getData());
									criteria.setPmDistrict(((ScQtLocCriteria) districtNode
											.getData()).getPmDistrict());
									criteria.setPmDivision(((ScQtLocCriteria) districtNode
											.getParent().getData())
											.getPmDivision());
									criteria.setPmState(((ScQtLocCriteria) treeNode2
											.getData()).getPmState());
									criteria.setScQtSupplierCriteria(this
											.getManagedBean()
											.getSupplierCriteria());
									setUserDetails(criteria);
									results.add(criteria);
								}
							} else {
								ScQtLocCriteria criteria = ((ScQtLocCriteria) devision
										.getData());
								criteria.setPmDistrict(((ScQtLocCriteria) devision
										.getData()).getPmDistrict());
								criteria.setPmState(((ScQtLocCriteria) treeNode2
										.getData()).getPmState());
								criteria.setScQtSupplierCriteria(this
										.getManagedBean().getSupplierCriteria());
								setUserDetails(criteria);
								results.add(criteria);
							}
						}
					} else {
						ScQtLocCriteria criteria = (ScQtLocCriteria) treeNode2
								.getData();
						criteria.setScQtSupplierCriteria(this.getManagedBean()
								.getSupplierCriteria());
						setUserDetails(criteria);
						results.add(criteria);
					}
				}
			}
		}
		return results;
	}

	private void updateQtDtls() {
		List<TabBean> commiteTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommittes = null;
		if (null != commiteTabs) {
			qtCommittes = new ArrayList<ScQtCommittee>();
			for (TabBean tabBean : commiteTabs) {
				if (!QTConstants.FINALIZATION_SECRETARY_TAB
						.equalsIgnoreCase(tabBean.getTitleName())) {
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

	public String searchQtBackAction() {
		debugLogger("searchQtBackAction");
		return "/xhtml/sc/quatationtender/qtntendermain.xhtml?faces-redirect=true";
	}

	public void retrieveQuationTenderDetails(Long qtId, String docType) {
		debugLogger("retrieveQuationTenderDetails");
		// this.getManagedBean().setActiveIndex(0);
		IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			if (null == docType || "".equalsIgnoreCase(docType)) {
				tenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);
			} else {
				tenderReqDTO.setDocType(docType);
			}
			tenderReqDTO.setQtId(qtId);
			tenderReqDTO.setBdgDtlsReq(true);
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			ScQt scQt = this.getManagedBean().getQt();
			if (null == scQt
					|| (scQt.isZonal()
							&& !scQt.isPanel()
							&& null != this.getManagedBean()
									.getZoneGeneralTrees() && this
							.getManagedBean().getZoneGeneralTrees().isEmpty())) {
				tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
				if (tenderResDTO.getQtDetail() != null) {
					getManagedBean().setQt(tenderResDTO.getQtDetail());
					Long currentStatus = this.getManagedBean().getQt()
							.getQtStatus().getStatusId();
					this.getManagedBean().setCurrentStatusId(currentStatus);
					getManagedBean().getCtContract().setContractNo(
							this.getManagedBean().getQt()
									.getExistingContractNo());
					if (this.getManagedBean().getQt().isZonal()) {
						if (this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
							if (null != this.getManagedBean().getQt().getZoneLocationLevel()) {
								this.getManagedBean().setStateCodes(
										this.getManagedBean().getQt().getZoneLocationLevel().getParameterCode());
							}
							this.getManagedBean().setSupplierCriteria(this.getManagedBean().getQt().getScQtSupplierCriteria());
							if (tenderResDTO.getQtDetail().isZonal()) {
								this.getManagedBean().setCtzones(Boolean.TRUE);
								if (isNotNullOREmpty(tenderResDTO
										.getZoneDetails())) {
									for (TplSpecZoneDtl zone : tenderResDTO
											.getZoneDetails()) {
										getManagedBean()
												.getZoneGeneralTrees()
												.add(LocationSingleton
														.getInstance()
														.constuctTreeNode(
																zone,
																zone.getTplSpecZoneLocs()));
									}
								}
							}
						}
					}
					if (getManagedBean().getQt().getEstimatedAmount() != null) {
						if (getManagedBean().getQt().getEstimatedAmount()
								.intValue() >= SourcingConstants.ESTMT_TWANTY_THOUSAND
								&& getManagedBean().getQt()
										.getEstimatedAmount().intValue() <= SourcingConstants.ESTMT_FIFTY_THOUSAND) {
							if (getManagedBean().getQt().getQtValidityPeriod() == null) {
								getManagedBean().getQt().setQtValidityPeriod(
										SourcingConstants.SIXTY_DAYS);
							}
							/*
							 * if (getManagedBean().getQt().getPublishPeriod()
							 * == null) {
							 * getManagedBean().getQt().setPublishPeriod(
							 * SourcingConstants.PUBLISH_THREE_DAYS); }
							 */
						} else if (getManagedBean().getQt()
								.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIFTY_THOUSAND
								&& getManagedBean().getQt()
										.getEstimatedAmount().intValue() <= SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
							if (getManagedBean().getQt().getQtValidityPeriod() == null) {
								getManagedBean().getQt().setQtValidityPeriod(
										SourcingConstants.NINTY_DAYS);
							}
							/*
							 * if (getManagedBean().getQt().getPublishPeriod()
							 * == null) {
							 * getManagedBean().getQt().setPublishPeriod(
							 * SourcingConstants.PUBLISH_SEVEN_DAYS); }
							 */
						} else if (getManagedBean().getQt()
								.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
							if (getManagedBean().getQt().getQtValidityPeriod() == null) {
								getManagedBean().getQt().setQtValidityPeriod(
										SourcingConstants.NINTY_DAYS);
							}
							/*
							 * if (getManagedBean().getQt().getPublishPeriod()
							 * == null) { getManagedBean() .getQt()
							 * .setPublishPeriod(
							 * SourcingConstants.PUBLISH_TWENTYONE_DAYS); }
							 */
						}
					}
				}
				if (this.getManagedBean().getGosodos() == null) {
					this.getManagedBean().setGosodos(
							tenderResDTO.getBudgetDtls());
				}
			}
		} finally {
			QtDetailsSO = null;
		}
	}

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
					parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
				} else if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
					parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
				} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);
					parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
				} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);
					parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
				} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);
					parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
				} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);
					parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
				}

				qtCommitte = new ScQtCommittee();
				qtCommitte.setScQt(this.getManagedBean().getQt());
				setUserDetails(qtCommitte);
				qtCommitte.setCommitteeActivities(null);
				qtCommitte.setQtCommitteRemarks(null);
				qtCommitte.setScQtMeetings(null);
				ScCommittee committee = new ScCommittee();
				PmStatus status = new PmStatus();
				status.setStatusId(QTStatusConst.CMT_PENDING_SUBMISSION);
				committee.setCommitteStatus(status);
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

	private DualListModel<ScQtApproverSettings> approverSettingDtls(
			RoleCode code, ScQtApproval approval) {
		List<ScQtApproverSettings> availableApprovers = new ArrayList<ScQtApproverSettings>();
		List<ScQtApproverSettings> selectedApprovers = new ArrayList<ScQtApproverSettings>();
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(code.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> approvers = userResDTO.getUserList();
		if (null != approval) {
			if (null != approval.getApproverSettings()
					&& !approval.getApproverSettings().isEmpty()) {
				boolean isApproverExists;
				for (PmUser pmUser : approvers) {
					isApproverExists = false;
					for (ScQtApproverSettings scQtApproverSettings : approval
							.getApproverSettings()) {
						if (scQtApproverSettings.getApprover().getUserId()
								.equals(pmUser.getUserId())) {
							scQtApproverSettings.setQtApproval(approval);
							isApproverExists = true;
							break;
						}
					}
					if (!isApproverExists) {
						ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
						qtApproverSettings.setApprover(pmUser);
						qtApproverSettings.setQtApproval(approval);
						availableApprovers.add(qtApproverSettings);
					}
				}
				selectedApprovers = approval.getApproverSettings();
			} else {
				for (PmUser pmUser : approvers) {
					ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
					qtApproverSettings.setApprover(pmUser);
					qtApproverSettings.setQtApproval(approval);
					availableApprovers.add(qtApproverSettings);
				}
				selectedApprovers = new ArrayList<ScQtApproverSettings>();
			}
		}
		return new DualListModel<ScQtApproverSettings>(availableApprovers,
				selectedApprovers);
	}

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
		return "/xhtml/sc/quatationtender/technicalTemplateMaintenance.xhtml?faces-redirect=true";
	}

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

	public String technicalSpecBackAction() {
		debugLogger("technicalSpecBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	public String financialSpecEditAction(ScQtCheckList checklist) {
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
		return "/xhtml/sc/quatationtender/financialTemplateMaintenance.xhtml?faces-redirect=true";
	}

	public String financialSpecBackAction() {
		debugLogger("financialSpecBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
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

	public void retrieveSecretaryNameByCommType(AjaxBehaviorEvent event) {
		debugLogger("retrieveSecretaryNameByCommType");
		Long id = (Long) event.getComponent().getAttributes().get("value");
		if (id != null) {
			List<PmParameter> list = this.getManagedBean()
					.getFinalizationSecratary();
			if (list != null) {
				for (PmParameter parameter : list) {
					if (parameter.getParameterId().equals(id)) {
						PmParameter pmParameter = new PmParameter();
						pmParameter.setParameterCode(parameter
								.getParameterCode());
						pmParameter.setParameterId(parameter.getParameterId());
						pmParameter.setBrandNew(false);
						this.getManagedBean().getQt()
								.setFinalizationCommitte(pmParameter);
					}
				}
			}
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(id);
			ParameterResDTO parameterResDTO = getParameterSO()
					.retrieveParameterById(parameterReqDTO);
			String commType = parameterResDTO.getPmParameter()
					.getParameterCode();

			if (!commType
					.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)
					&& !commType
							.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {

				List<PmUser> userList = retrievePmUserSecratryDtl(commType);
				if (userList != null && userList.size() > 0) {
					getManagedBean().getQt().setFinalizationSecratery(
							userList.get(0));
				}
			}
		} else {
			this.getManagedBean().getQt().setFinalizationSecratery(null);
			if (null == this.getManagedBean().getQt()
					.getFinalizationSecratery()) {
				this.getManagedBean().getQt()
						.setFinalizationSecratery(new PmUser());
			}
			List<PmParameter> finalizeList = retrieveFinalizationSecratryDtl(getManagedBean()
					.getQt().getQtId());
			log.info("Finalization Secratory Size : " + finalizeList.size());
			this.getManagedBean().setFinalizationSecratary(finalizeList);
		}
	}

	public List<PmUser> retrievePmUserSecratryDtl(String committeeType) {
		debugLogger("retrievePmUserSecratryDtl");
		this.getManagedBean().setPmUsersList(null);
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setCommitteeType(committeeType);

			CommitteResDTO resDTO = committeSO
					.retrieveFcSecretaryUsersByCommType(reqDTO);
			this.getManagedBean().setPmUsersList(resDTO.getUserList());
			return resDTO.getUserList();

		} finally {
			committeSO = null;
		}
	}

	public void committeTabChangeAction() {
		if (log.isDebugEnabled()) {
			log.debug("Committe Tab change Action");
		}
		
		Integer selectedCommitteeTabIndex = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedCommitteeTabIndex"));
		
		this.getManagedBean().setToBeDelCommitteMemebers(null);
		this.getManagedBean().setSelectedTabIndex(selectedCommitteeTabIndex);
		TabBean committeTabBean = this.getManagedBean()
				.getScCommitteeMemberTabList().get(selectedCommitteeTabIndex);

		this.getManagedBean().setSelectedTabBean(committeTabBean);
	}

	public void approvalTabChangeAction() {
		if (log.isDebugEnabled()) {
			log.debug("ApprovalTabChange Action");
		}
		
		Integer selectedApprovalTabIndex = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedApprovalTabIndex"));
		
		TabBean approvalTabBean = this.getManagedBean().getTabList()
				.get(selectedApprovalTabIndex);
		/*if (approvalTabBean.getScQtApproval().getApprovalType().equals(QTConstants.OTHERS_APPROVAL)) {
			for (ScQtApprovalRequest approvalRequest : approvalTabBean.getScQtApproval().getScQtApprovalDtls()) {
				if (null == approvalRequest.getApproverAction()) {
					approvalRequest.setApproverAction(new PmParameter());
				}
				if (approvalRequest.getAppRequestRemark() == null) {
					approvalRequest.setAppRequestRemark(new ScQtAppRequestRemark());
				}
			}
		}*/
		this.getManagedBean().setSelectedApprovalTabIndex(selectedApprovalTabIndex);
		this.getManagedBean().setSelectedApprovalTabBean(approvalTabBean);
	}

	public void validateQtDetails() {
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		int activeIndex = this.getManagedBean().getActiveIndex();

		int subTabIndex = this.getManagedBean().getSelectedTabIndex();
		
		String zonelocationLevel = this.getManagedBean().getStateCodes();
		String locationLevel = this.getManagedBean().getLocalLevelCode();

		QuationTenderResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Long currentStatus = this.getManagedBean().getQt().getQtStatus()
				.getStatusId();
		QuationTenderReqDTO reqDTO = null;
		switch (activeIndex) {
		case 0:
			reqDTO = prepareQTInfo();
			detailSO.validateateQTDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId(), null);
			// this.getManagedBean().setQt(resDTO.getQtDetail());
			break;
		case 1:
			reqDTO = prepareQTInfo();
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setScQt(reqDTO.getQtdetail());
			committeReqDTO.setUser(new FacesScopes().getUserSessionBean());
			committeSO.validateAppointCommitteDtls(committeReqDTO);
			// reqDTO = prepareQTInfo();
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setSelectedTabIndex(subTabIndex);
			// this.getManagedBean().setActiveIndex(0);
			retrieveCommitteeAppointmentTabDtl();
			break;
		case 2:
			reqDTO = prepareQTInfo();
			detailSO.validateSupplierQualificationDtls(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			retrieveSuppQualificationTabDtl();
			break;
		case 3:
			reqDTO = prepareQTInfo();
			reqDTO.getQtdetail().setModified(false);
			detailSO.validateApprovalDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			retrieveApprovalTabDtl();
			break;
		case 4:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				detailSO.validateBsvDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				retrieveBriefingSitevisitTabDtl();
			}
			break;
		case 5:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				detailSO.validateScheduleDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				retrieveScheduleTabDtl();
			}
			break;
		case 6:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				// detailSO.validateDeductionDtls(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				retrieveDeductionPaymentTabDtl();
			}
			break;
		case 7:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				retrieveDocumentTabDtl();
				break;
			}
		case 8:
			break;
		case 9:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				reqDTO.getQtdetail().setModified(true);
				detailSO.validateApprovalDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				retrievePublicationApprovalTabDtl();
			}
			break;
		}
		this.getManagedBean().setActiveIndex(activeIndex);
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setStateCodes(zonelocationLevel);
		this.getManagedBean().setLocalLevelCode(locationLevel);
	}

	public void qtDetailsTabChange(Integer activeIndex) {
		int priviousIndex = this.getManagedBean().getActiveIndex();
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		if (priviousIndex != activeIndex) {
			switch (priviousIndex) {
			case 0:
				detailSO.validateateQTDetails(prepareQTInfo());
				// retrieveQuationTenderDetails(this.getManagedBean().getQt().getQtId());
				break;
			case 1:
				// retrieveCommitteeAppointmentTabDtl();
				break;

			case 2:
				detailSO.validateSupplierQualificationDtls(prepareQTInfo());
				// retrieveSuppQualificationTabDtl();
				break;
			case 3:
				QuationTenderReqDTO reqDTO = prepareQTInfo();
				reqDTO.getQtdetail().setOtherApproval(false);
				reqDTO.getQtdetail().setModified(false);
				detailSO.validateApprovalDetails(reqDTO);
				// retrieveApprovalTabDtl();
				break;

			case 4:
				if (getManagedBean().getQt().getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) {
					detailSO.validateBsvDetails(prepareQTInfo());
				}
				break;

			case 5:
				if (getManagedBean().getQt().getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) {
					detailSO.validateScheduleDetails(prepareQTInfo());
				}
				break;

			case 6:
				if (getManagedBean().getQt().getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) {
					// detailSO.validateDeductionDtls(prepareQTInfo());
				}
				break;

			case 7:
				break;
			case 8:
				break;
			case 9:
				if (getManagedBean().getQt().getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) {
					QuationTenderReqDTO qtReqDTO = prepareQTInfo();
					qtReqDTO.getQtdetail().setModified(true);
					detailSO.validateApprovalDetails(qtReqDTO);
				}
				break;
			}

			switch (activeIndex) {
			case 0:
				// detailSO.validateateQTDetails(prepareQTInfo());
				retrieveQuationTenderDetails(this.getManagedBean().getQt()
						.getQtId(), null);
				break;
			case 1:
				retrieveCommitteeAppointmentTabDtl();
				break;

			case 2:
				// detailSO.validateSupplierQualificationDtls(prepareQTInfo());
				retrieveSuppQualificationTabDtl();
				break;
			case 3:
				// detailSO.validateApprovalDetails(prepareQTInfo());
				retrieveApprovalTabDtl();
				break;

			case 4:
				if (getManagedBean().getQt().getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) {
					retriveTechnicalSpecification();
				} else {
					retrieveBriefingSitevisitTabDtl();
				}
				break;
			case 5:
				if (getManagedBean().getQt().getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) {
					retriveFinancialSpecification();
				} else {
					retrieveScheduleTabDtl();
				}
				break;
			case 6:
				if (getManagedBean().getQt().getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) {
					retrieveActivitiesTabDtl();
				} else {
					retrieveDeductionPaymentTabDtl();
					// retrieveActivitiesTabDtl(); // Kenny - temporary
				}
				break;
			case 7:
				if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
					retrieveDocumentTabDtl();
				}
				break;
			case 8:
				retrieveActivitiesTabDtl();
				break;
			case 9:
				if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
					retrievePublicationApprovalTabDtl();
				}
				break;
			}
			this.getManagedBean().setActiveIndex(activeIndex);
		}
	}

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

					if (null != approvalTab.getScQtApproval()
							.getScQtApprovalAttachment()) {
						approvalTab.getScQtApproval()
								.getScQtApprovalAttachment()
								.setScQtApproval(qtApproval);
						qtApproval.setScQtApprovalAttachment(approvalTab
								.getScQtApproval().getScQtApprovalAttachment());
					}
					return qtApproval;
				}
			}
		}
		return qtApproval;
	}

	private IParameterSO getParameterSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	private ScQtBsv constructBsvByBean(BriefingZoneBean bsvDtlBean) {
		ScQtBsv scQtBsv = null;
		if (null != bsvDtlBean) {
			scQtBsv = bsvDtlBean.getScQtBsv();
			List<ScQtBsvDtl> bsvDetils = new ArrayList<ScQtBsvDtl>();
			List<ScQtBsvDtl> briefings = bsvDtlBean.getBriefingList();
			List<ScQtBsvDtl> siteVisit = bsvDtlBean.getSitevisitList();
			if (!scQtBsv.isSameBsv()) {
				if (!briefings.isEmpty()) {
					bsvDetils.addAll(briefings);
				}
				if (!siteVisit.isEmpty()) {
					bsvDetils.addAll(siteVisit);
				}
			} else {
				if (!briefings.isEmpty()) {
					bsvDetils.addAll(briefings);
					scQtBsv.setAllSiteVisitReq(false);
					scQtBsv.setMinSiteVisitAttend(0);
				}
			}
			scQtBsv.setScQtBsvDtls(bsvDetils);
		}
		return scQtBsv;
	}

	private boolean isEmptyOrNull(List<? extends Object> list) {
		if (null == list || list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * method to give whether to enable buttons or not..
	 * 
	 * @param statusId
	 * @param evalType
	 * @return
	 */
	private boolean getEnableFlagByStatus(Long statusId, String evalType,
			ScCommittee committe) {

		if (null != committe
				&& QTStatusConst.CMT_PENDING_APPROVAL.equals(committe
						.getCommitteStatus().getStatusId())) {
			return true;
		}

		if (QTStatusConst.QT_PENDING_SPECIFICATION_PREPARATION.equals(statusId)) {
			return true;
		}

		if (SourcingConstants.ONE_TIER.equalsIgnoreCase(evalType)) {
			if (QTStatusConst.QT_EVALUATION_PROPOSAL.equals(statusId)) {
				return true;

			} else if (QTStatusConst.QT_SUPPLIER_FINALIZATION.equals(statusId)) {
				return true;

			}

		} else {
			if (QTStatusConst.QT_EVALUATION_PROPOSAL.equals(statusId)) {
				return true;

			}
		}
		return false;
	}

	/**
	 * will be called in maintance flow only..
	 */
	public String submitQtDetailsForApproval() {

		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			// validateQtDetails();
			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();

			QuationTenderResDTO quationTenderResDTO = detailSO
					.updateQtMaintananceDetails(quationTenderReqDTO);
			String msgCode;
			if (quationTenderResDTO.isSuccessful()) {
				msgCode = MessageConst.INFO_700026;
			} else {
				msgCode = MessageConst.INFO_700004;
			}
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
	 */
	public String cancelQtDetails() {
		IQTDetailSO detailSO;

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();
			if (QTStatusConst.QT_BRIEFING_SITE_VISIT <= scQt.getQtStatus()
					.getStatusId()) {

				quationTenderReqDTO.setQtId(this.getManagedBean().getQt()
						.getQtId());
				quationTenderReqDTO
						.setApprovalType(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
				QuationTenderResDTO resDTO = detailSO
						.retriveQTApprovalDetails(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQtApproval qtApproval = resDTO.getQtApproval();

				qtApproval.setScQtApprovalRemarks(initializeRemarks(qtApproval,
						this.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(),
						qtApproval.getScQtApprovalRemarks(),
						ScQtApprovalRemark.class));
				TabBean tabBean = new TabBean(qtApproval.getApprovalType(),
						qtApproval, pbaApproverSettingDtls(qtApproval, this
								.getManagedBean().getQt().getQtId()));
				this.getManagedBean().setSelectedApprovalTabBean(tabBean);
				return "/xhtml/sc/quatationtender/cancelqtdetails.xhtml";
			} else {
				ScQtStatus qtStatus = new ScQtStatus();
				qtStatus.setScQt(scQt);
				PmStatus status = new PmStatus();
				status.setStatusId(QTStatusConst.QT_CANCELLED);
				qtStatus.setPmStatus(status);
				qtStatus.setCurrent(true);
				qtStatus.setDocType(QTConstants.DOCTYPE_QT);
				List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
				qtStatuses.add(qtStatus);
				scQt.setScQtStatus(qtStatuses);
				scQt.setScQtCommittees(null);
				scQt.setScQtApprovals(null);
				scQt.setScQtBriefings(null);
				quationTenderReqDTO.setQtdetail(scQt);

				QuationTenderResDTO resDTO = detailSO
						.saveQTDtl(quationTenderReqDTO);
				if (resDTO.isSuccessful()) {

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
																			.getQtId()
																			.toString() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					return CommonUIConst.ACKNOWLEDGE_PAGE;

				}
			}
		} finally {

		}

		return null;
	}

	private List<PmParameter> pbaApproverSettingDtls(ScQtApproval approval,
			Long qtId) {
		ICommitteSO committeSO = (ICommitteSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(qtId);
		CommitteResDTO committeResDTO = committeSO
				.retrieveFinalizationSecratryDtls(reqDTO);
		List<PmParameter> finalizationSecreatry = committeResDTO
				.getFinalizationSecratry();

		return finalizationSecreatry;
	}

	public void retrieveFinalizationCommitteeSecretaryName(
			AjaxBehaviorEvent event) {
		debugLogger("retrieveFinalizationCommitteeSecretaryName");
		String commType = (String) event.getComponent().getAttributes()
				.get("value");
		if (null != commType) {
			if (!commType
					.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)
					&& !commType
							.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {

				List<PmUser> userList = retrievePmUserSecratryDtl(commType);
				if (userList != null && userList.size() > 0) {
					getManagedBean().getSelectedApprovalTabBean()
							.getScQtApproval().getApproverSettings().get(0)
							.setApprover(userList.get(0));
				}
			}
		} else {
			this.getManagedBean().getQt().setFinalizationSecratery(null);
			List<PmParameter> finalizeList = retrieveFinalizationSecratryDtl(getManagedBean()
					.getQt().getQtId());
			log.info("Finalization Secratory Size : " + finalizeList.size());
			this.getManagedBean().setFinalizationSecratary(finalizeList);
		}
	}

	public String constuctConditionFields() {
		if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
			Long orgProfileId = this.getManagedBean().getQt().getPmOrgProfile()
					.getOrgProfileId();
			if (null != orgProfileId) {
				return "quotationTender.pmOrgProfile.orgProfileId^"
						+ orgProfileId;
			}
		}
		return "";
	}

	private Date getLatestBsvDate(List<ScQtBsvDtl> bsvDetails) {
		List<Date> dates = new ArrayList<Date>();
		Date latestBsvDate = null;
		for (ScQtBsvDtl bsvDtl : bsvDetails) {
			dates.add(bsvDtl.getBsvDate());
			/*if (null == latestBsvDate) {
				latestBsvDate = bsvDtl.getBsvDate();
			} else {
				latestBsvDate = bsvDtl.getBsvDate();
			}
			if (DateUtils.isDateGreaterOrEqual(latestBsvDate,
					bsvDtl.getBsvDate())) {
				latestBsvDate = bsvDtl.getBsvDate();
			}*/
		}
		if (null != dates && !dates.isEmpty()) {
			latestBsvDate = Collections.max(dates);
		}
		return latestBsvDate;
	}

	public String submitQtCancelDetails() {

		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class); //

			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt qt = this.getManagedBean().getQt();
			qt.setScQtCommittees(null);
			qt.setScQtBriefings(null);
			qt.setScQtStatus(null);
			TabBean tabBean = this.getManagedBean()
					.getSelectedApprovalTabBean();
			finalizeRemarks(tabBean.getScQtApproval().getScQtApprovalRemarks());
			if (tabBean.getScQtApproval().getScQtApprovalRemarks().isEmpty()) {
				ScQtApproval qtApproval = tabBean.getScQtApproval();
				qtApproval.setScQtApprovalRemarks(initializeRemarks(qtApproval,
						this.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(),
						qtApproval.getScQtApprovalRemarks(),
						ScQtApprovalRemark.class));
				throw new EpException("Remarks is mandatory");
			}
			List<ScQtApproverSettings> approverSettings = tabBean
					.getScQtApproval().getApproverSettings();
			if (null != approverSettings && !approverSettings.isEmpty()) {
				String fcCommType = approverSettings.get(0).getCommitteType();
				if (StringUtils.isEmpty(fcCommType)) {
					throw new EpException("Please Choose finalization Committe");
				}
			}
			List<ScQtApproval> scQtApprovals = new ArrayList<ScQtApproval>();
			tabBean.getScQtApproval().setSubmissionDate(new Date());
			scQtApprovals.add(tabBean.getScQtApproval());
			qt.setScQtApprovals(scQtApprovals);
			quationTenderReqDTO.setQtdetail(qt);
			QuationTenderResDTO quationTenderResDTO = detailSO
					.cancleQT(quationTenderReqDTO);

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
													MessageConst.INFO_700018,
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

	private String getMsgCodeByTaskName(String taskName) {
		if (SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName)) {
			return MessageConst.INFO_700052;
		}
		return MessageConst.INFO_700005;
	}

	public void updateUserIdToSettings(AjaxBehaviorEvent event) {
		Long id = (Long) event.getComponent().getAttributes().get("value");
		List<PmUser> userList = this.getManagedBean().getPmUsersList();
		for (PmUser user : userList) {
			if (user.getUserId().equals(id)) {
				this.getManagedBean().getSelectedApprovalTabBean()
						.getScQtApproval().getApproverSettings().get(0)
						.setApprover(user);
				break;
			}
		}
	}
}
