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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
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
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
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
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAttachment;
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
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

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

	/**
	 * Instantiates a new QT detail backing bean.
	 */
	public QTDetailBackingBean() {
		log.info("inside constructor");

	}

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

	/**
	 * Inits the.
	 */
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
		
		if (null == this.getManagedBean().getMinistryProfile() && null == this.getManagedBean().getPtjProfile()) {
			getDefaultPtjAndMinistryValues();
		}
	}

	/**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {
		
		debugLogger("getDefaultPtjAndMinistryValues");
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())) {
			getManagedBean().setMinistryProfile(null);
			getManagedBean().setPtjProfile(null);
		} else {
			PmOrgProfile ptjOrgProfile = getPTJOrgProfileByLoginUser();
			if (getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())) {
				this.getManagedBean().setPtjProfile(ptjOrgProfile);
				if (ptjOrgProfile != null) {
					this.getManagedBean().setMinistryProfile(
							retrieveMinistryOrgProfile(ptjOrgProfile.getOrgProfileId()));
				}
				getManagedBean().setDeskOfficer(true);
				
			} else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
				getManagedBean().setPtjProfile(null);
				if(ptjOrgProfile != null){
					getManagedBean().setMinistryProfile(retrieveMinistryOrgProfile(ptjOrgProfile.getOrgProfileId()));
				}
			}
		}
	}
	
	/**
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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

	/**
	 * Submit qt details action.
	 * 
	 * @return the string
	 */
	public String submitQTDetailsAction() {
		this.getManagedBean().setSaveFlow(false);
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			validateQtDetails();
			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setSaveFlow(false);
			FacesScopes facesScopes = new FacesScopes();
			quationTenderReqDTO.setToken(facesScopes
					.getBPMAuthenticationCtxToken());

			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			quationTenderReqDTO.setPayLoad(this.getManagedBean()
					.getPayLoad());
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
	 * Clone Popup Action - To clear the clone search result for next time.
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
	 * Handle ptj name.
	 * 
	 * @return the list
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
	 * Handle ptj name changed.
	 * 
	 * @param orgProfile
	 *            the org profile
	 */
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
	public String editQuationTenderDetails(ScQt scQt, boolean viewQT) {
		renewQTDetails();
		this.getManagedBean().setDeskOfficer(viewQT);
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
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			ScQt selectedScQt = resDTO.getQtDetail();
			setAmmendMentBean(resDTO.getAmendMentBean());
			this.getManagedBean().setRuleDataBean(resDTO.getRuleDataBean());

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

				
			} else {

				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (selectedScQt.isZonal()) {
						this.getManagedBean().setCtzones(Boolean.TRUE);
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

			}
			if (null != getManagedBean().getQt()
					&& null == getManagedBean().getQt()
							.getProcurementTypeCatId()) {
				getManagedBean().getQt().setProcurementTypeCatId(
						new PmParameter());
			}
			if (!viewQT) {
				this.getManagedBean().setAuditorRoleView(true);
				this.getManagedBean().setEditable(true);
				if (getManagedBean().getCurrentStatusId() >= 60002) {
					
				} else {
					this.getManagedBean().getQt().getQtStatus().setStatusId(60015L);
				}
			}
		
			setScQtTrackingDairyObject(scQt);
			this.getManagedBean().setViewQtDetails(viewQT);
			
		} finally {
			detailSO = null;
		}
		return REDIRECT_PAGE;
	}

	private void renewQTDetails() {
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean()
				.getQtnTenderSearchBean();

		super.renewSesionObject();

		getManagedBean().setQtnTenderSearchBean(qtnTenderSearchBean);
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

					qtCommitteList.add(tabBean.getScQtCommittee());
				}
			}
		}
		scQt.setScQtCommittees(qtCommitteList);
		// Code for to update the supplier qualification criteria
		/* List<ScQtCategory> categoryList = new ArrayList<ScQtCategory>(); */
		if (!this.getManagedBean().getQt().isPanel()) {
			if (this.getManagedBean().getQt().getQtType()
					.equals(QTConstants.OPEN_TYPE)) {
				// TODO need to check why updating the twice.. sagar
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (openList != null && !openList.isEmpty()) {
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
					.equals(QTConstants.RESTRICTED_TYPE)) {
				List<ScQtCategory> restrictedList = this.getManagedBean()
						.getSuppCriteriarestrictedList();
				if (restrictedList != null && !restrictedList.isEmpty()) {

					scQt.setScQtCategorys(restrictedList);
				}
				List<ScQtSupplier> qtSuppliers = this.getManagedBean()
						.getQtSupplierList();
				if (qtSuppliers != null && !qtSuppliers.isEmpty()) {
					scQt.setScQtSuppliers(qtSuppliers);
				}

			}
		}
		if (scQt.getScQtSupplierCriteria() != null
				&& !scQt.getScQtSupplierCriteria().isAllState()) {
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), null);
			if (null != locCriteria && !locCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
			} else {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
			}
		}

		if (null != this.getManagedBean().getSupplierCriteria()) {
			setUserDetails(this.getManagedBean().getSupplierCriteria());
			scQt.setScQtSupplierCriteria(this.getManagedBean()
					.getSupplierCriteria());
		}
		if (!scQt.isPanel() && scQt.isZonal()) {
			if (this.getManagedBean().isPaidupcapital()) {
				if (null != scQt.getScQtSupplierCriteria()) {
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						scQt.getScQtSupplierCriteria()
								.getQtZoneCriterias()
								.setScQtSupplierCriteria(
										this.getManagedBean()
												.getSupplierCriteria());
					}
				}
			}
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
						bsvs.remove(i--);
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
						.setDefaultDateTime(closingDate));
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
									&& pmAddress.getPmDivision()
											.getDivisionId() == null) {
								scQtSampleSubmission.getAddress()
										.setPmDivision(null);
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
							approverSettings.setQtApproval(pubApproval);
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

		List<ScQtLocCriteria> qtLocCriteriaRemoveList = this.getManagedBean()
				.getSupplierLocationList();
		if (null != qtLocCriteriaRemoveList
				&& !qtLocCriteriaRemoveList.isEmpty()) {
			for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
				criteria.setDeleted(true);
			}
		}
		quationTenderReqDTO
				.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);

		quationTenderReqDTO.setAmendMentBean(this.getManagedBean()
				.getAmendMentBean());

		quationTenderReqDTO.setRuleDataBean(this.getManagedBean()
				.getRuleDataBean());

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
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			if (isAuditor()) {
				PmOrgProfile ptjProfiles = getManagedBean().getPtjProfile();
				if (ptjProfiles != null&& ptjProfiles.getOrgProfileId() != null) {// Filter By - PTJ
					
					SearchFilterBean ptjProfile = new SearchFilterBean("pmOrgProfile." + "orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),ptjProfiles.getOrgProfileId());
					searchFilterBean.add(ptjProfile);
				} else {
					PmOrgProfile ministryProfile = getManagedBean().getMinistryProfile();
					if (ministryProfile != null&& ministryProfile.getOrgProfileId() != null) {// Filter By - Ministry
							
						SearchFilterBean ministryOrgProfile = new SearchFilterBean(
								"pmOrgProfile."
										+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY
										 .value()),SearchOperator.OPERATOR_EQUALS.value(), ministryProfile.getOrgProfileId());
						searchFilterBean.add(ministryOrgProfile);
					}
				}
				 // Removed the restriction condition for auditor users.
				if(CollectionUtils.isNotEmpty(qtSearchBean.getDataFilterMetadatas())){
					qtSearchBean.getDataFilterMetadatas().clear();
				}
			}
			
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
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

	/**
	 * Retrieve briefing sitevisit tab dtl.
	 */
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
	public void retrieveCommitteeAppointmentTabDtl(boolean isApprovalReq) {
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
			String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
			ParameterHelper parameterHelper = ParameterHelper.getInstance();

			committeTabs = new ArrayList<TabBean>();
			// BO and specification committe is common for both evaluations..
			/*String tabType = ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER;
			ScQtCommittee boCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					qtId);
			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, boCommitte));*/

			String tabType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					qtId);
			if (QTStatusConst.CMT_COMPLETED.equals(specCommitte
					.getScCommittee().getCommitteStatus().getStatusId())) {
				this.getManagedBean().setCloneNeeded(true);
			}
			//copyBoMembersToSpecCommitte(specCommitte, boCommitte);

			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, specCommitte));
			 
			if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT <= currentStatus) {
				// evaluation type is one tier only oc is added..
				if (QTConstants.EVALUATION_ONE_TIER.equals(evaluationType)) {
					tabType = ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
					ScQtCommittee evalCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						evalCommitte.getScCommittee().setApprovalReq(
								isApprovalReq);
						evalCommitte.getScCommittee().setModified(true);
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
						openingCommitte.getScCommittee().setApprovalReq(
								isApprovalReq);
						openingCommitte.getScCommittee().setModified(true);
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							openingCommitte));

					tabType = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
					ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						tecCommitte.getScCommittee().setApprovalReq(
								isApprovalReq);
						tecCommitte.getScCommittee().setModified(true);
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							tecCommitte));

					tabType = ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE;
					ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if (isApprovalReq) {
						fecCommitte.getScCommittee().setApprovalReq(
								isApprovalReq);
						fecCommitte.getScCommittee().setModified(true);
					}
					committeTabs.add(new TabBean(parameterHelper
							.getParameterDesc(parameterType, tabType), tabType,
							fecCommitte));
				}

				String fcTitleName = null;
				PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
				if (fcSecretaryCode == null
						|| fcSecretaryCode.getParameterId() == null) {
					List<PmParameter> fcSecretaries = retrieveFinalizationCommitteeType();
					if (fcSecretaries != null && fcSecretaries.size() > 0) {
						fcSecretaryCode = fcSecretaries.get(0);
					} else {
						fcSecretaryCode = null;
					}
				}

				// Displaying the Secretary Details
				if (fcSecretaryCode != null) {
					StringBuilder fcTitleLabel = new StringBuilder(
							"lbl.sc.finalization.committee.secretary.");
					String fcSecretary = fcSecretaryCode.getParameterCode();

					if (fcSecretary != null) {
						if (fcSecretary
								.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
							fcTitleLabel
									.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
							tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
						} else if (fcSecretary
								.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD) >= 0) {
							fcTitleLabel
									.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
							tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
						}

						fcTitleName = ResourceBundleUtils.getMessage(
								fcTitleLabel.toString().toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
					}
				}

				ScQtCommittee committee = null;
				committeTabs.add(new TabBean(fcTitleName, tabType, committee));
			}
		} else if (null == committeTabs) { // formed only SC committee while creating the new QT. 
        
			String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
			ParameterHelper parameterHelper = ParameterHelper.getInstance();
			committeTabs = new ArrayList<TabBean>();
			String tabType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					qtId);
			if (QTStatusConst.CMT_COMPLETED.equals(specCommitte
					.getScCommittee().getCommitteStatus().getStatusId())) {
				this.getManagedBean().setCloneNeeded(true);
			}
			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, specCommitte));
		}

			this.getManagedBean().setScCommitteeMemberTabList(committeTabs);

			int zoneTabIndex = 0;
			for (TabBean tabBean : committeTabs) {
				MenuItem committeItem = new MenuItem();
				committeItem.setValue(tabBean.getTitleName());
				committeItem.setUpdate("@this");
				committeItem.setPartialSubmit(true);
				committeItem.setProcess("@this");
				// Added as part of Defect fix- 7578
				committeItem.setTransient(true);
				
				if ((!SourcingConstants.COMMITTE_TYPE_FINALIZATION
						.equals(tabBean.getTabType()) && tabBean
						.getScQtCommittee().getScCommittee()
						.getCommitteStatus().getStatusId()
						.equals(QTStatusConst.CMT_COMPLETED))
						|| (SourcingConstants.COMMITTE_TYPE_FINALIZATION
								.equals(tabBean.getTabType())
								&& scQt.getFinalizationCommitte() != null
								&& scQt.getFinalizationCommitte()
										.getParameterId() != null
								&& scQt.getFinalizationSecratery() != null && scQt
								.getFinalizationSecratery().getUserId() != null)) {
					committeItem.setIcon("ui-icon-endorse");
				}

				if (!ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
						.equals(tabBean.getTabType())
						&& !SourcingConstants.COMMITTE_TYPE_FINALIZATION
								.equals(tabBean.getTabType())) {

					committeItem.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.qt.committee.appointment."
									+ tabBean.getTabType().toLowerCase()
									+ ".tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
				}

				committeItem
						.setOncomplete("committeTabChangeAction([ {name : 'selectedCommitteeTabIndex', value : "
								+ zoneTabIndex + "}]);");
				menuModel.addMenuItem(committeItem);
				zoneTabIndex++;
			}
			this.getManagedBean().setMenuModel(menuModel);
			if (committeTabs.size() > 0) {
				// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
				this.getManagedBean().setSelectedTabBean(
						committeTabs.get(this.getManagedBean()
								.getSelectedCommitteeTabIndex()));
			}
		
	}

	/**
	 * Copy bo members to spec committe.
	 * 
	 * @param specCommitte
	 *            the spec committe
	 * @param boCommitte
	 *            the bo committe
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
	 * Retrieve finalization committee types.
	 * 
	 * @return the list
	 */
	public List<PmParameter> retrieveFinalizationCommitteeType() {
		ScQt scQt = getManagedBean().getQt();
		List<PmParameter> finalizationSecratary = getManagedBean()
				.getFinalizationSecratary();

		if (finalizationSecratary == null) {
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

				CommitteReqDTO reqDTO = new CommitteReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				CommitteResDTO resDTO = committeSO
						.retrieveFinalizationCommitteeType(reqDTO);

				finalizationSecratary = resDTO.getFinalizationSecratry();

				getManagedBean()
						.setFinalizationSecratary(finalizationSecratary);
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
		renewSesionObject();
		Long qtId = null;
		Object payLoad = getBpmTaskDetails(Object.class, null);
		String taskName = this.getManagedBean().getTaskName();
		if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
						.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ASSIGN_FC_SECRETARY.equalsIgnoreCase(this
						.getManagedBean().getTaskName())
				|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
						.equalsIgnoreCase(taskName)) {
			if (!SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
					.equalsIgnoreCase(taskName)
					&& !SCProcessNameConst.ASSIGN_FC_SECRETARY
							.equalsIgnoreCase(taskName)
					&& !SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {

				SCCommitteeDataType committeDataType = (SCCommitteeDataType) payLoad;

				qtId = committeDataType.getQtId().longValue();
				this.getManagedBean().setPendingRevision(true);
			} else {

				SCEvaluationDataType evalDataType = (SCEvaluationDataType) payLoad;
				qtId = evalDataType.getQtId().longValue();

			}
			this.getManagedBean().setActiveIndex(1);
			retrieveQuationTenderDetails(new Long(qtId), null);
			if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
					.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				retrieveCommitteeAppointmentTabDtl(true);
			} else {
				retrieveCommitteeAppointmentTabDtl(false);
			}
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
				List<ScQtApproval> approvals = this.getManagedBean().getQt()
						.getScQtApprovals();

				for (ScQtApproval scQtApproval : approvals) {
					if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
							.equalsIgnoreCase(scQtApproval
									.getApprovalCategory().getParameterCode())) {
						initializeRemarks(scQtApproval, this.getManagedBean()
								.getQt().getCurrentWorkFlowStatus(),
								scQtApproval.getScQtApprovalRemarks(),
								ScQtApprovalRemark.class);
						this.getManagedBean().setScQtApproval(scQtApproval);

						break;
					}
				}
			}
		} else {
			// qtId = quotationTenderDataType.getQtId().longValue();
			SCQuotationTenderDataType quationDataType = (SCQuotationTenderDataType) payLoad;
			qtId = quationDataType.getQtId().longValue();
			// retrieveFinalizationSecratryDtl(qtId);
			retrieveQuationTenderDetails(new Long(qtId), null);
		}

		if (null == this.getManagedBean().getQt().getProcurementMode()) {
			this.getManagedBean().getQt().setProcurementMode(new PmParameter());
		}

		if (SCProcessNameConst.REVISE_PCB.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			this.getManagedBean().setPcbProcess(true);
		}
		
		ScQt scQt = this.getManagedBean().getQt();
		if (scQt.getQtStatus() != null
				&& QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(scQt.getQtStatus().getStatusId()) 
				&& scQt.isPanel()
				&& scQt.getEstimatedAmount().intValue() < 50000) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							ResourceBundleUtils.getMessage("lbl.sc.qt.panel.estamt.msg",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())));
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);
	}

	
	/**
	 * Update bpm task.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	public String updateBpmTask(String outcome) {
		debugLogger("updateBpmTask");

		updateApprovalDetails(this.getManagedBean().getQt());
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
			
			if (ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA
					.equalsIgnoreCase(qt.getPmProcurementCategory().getParameterCode())) {
				qt.setMitiApproval(true);
				//qt.setMitiApproval(true);
			}

			Long currentStatus = qt.getQtStatus().getStatusId();
			if (currentStatus
					.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					|| currentStatus
							.equals(QTStatusConst.QT_REVISION_PUBLICATION)) {

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
					qt.setClosingDate(getManagedBean().getQt().getClosingDate());
					qt.setSampleClosingDate(qt.getClosingDate());
				} else {
					retrieveScheduleTabDtl();
					qt.setClosingDate(getManagedBean().getQt().getClosingDate());
					if (null == qt.getLatestBsvDate()) {
						qt.setLatestBsvDate(getManagedBean().getQt()
								.getLatestBsvDate());
					}
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
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
				updateApprovalRequestDtls(qt.getScQtApprovals(),
						ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(payLoad);
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
				committeReqDTO.setScQt(qt);
				committeReqDTO.setToBeDeletedMembers(this.getManagedBean()
						.getToBeDelCommitteMemebers());
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
			} else if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
					.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {

				// TODO - temporary for FC.
				if (payLoad instanceof SCEvaluationDataType) {
					if ("FC".equals(((SCEvaluationDataType) payLoad)
							.getCommitteeType())) {
						ICommitteSO committeSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										ICommitteSO.class);
						CommitteReqDTO committeReqDTO = new CommitteReqDTO();
						committeReqDTO.setToken(ctxToken);
						committeReqDTO.setTaskId(this.getManagedBean()
								.getTaskId());
						committeReqDTO.setPayLoad(payLoad);
						committeReqDTO.setOutcome(outcome);
						committeReqDTO.setPmUser(facesScopes
								.getUserSessionBean());
						committeReqDTO.setRoleCode(getUserRoleCode());
						committeReqDTO.setScQt(qt);
						committeResDto = committeSO
								.updateFinalizateSecrataryTask(committeReqDTO);
					} else {
						ICommitteSO committeSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										ICommitteSO.class);
						updateApprovalRequestDtls(
								qt.getScQtApprovals(),
								ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
						CommitteReqDTO committeReqDTO = new CommitteReqDTO();
						committeReqDTO.setToken(ctxToken);
						committeReqDTO.setTaskId(this.getManagedBean()
								.getTaskId());
						committeReqDTO.setPayLoad(payLoad);
						committeReqDTO.setOutcome(outcome);
						committeReqDTO.setPmUser(facesScopes
								.getUserSessionBean());
						committeReqDTO.setRoleCode(getUserRoleCode());
						committeReqDTO.setScQt(qt);
						committeReqDTO.setToBeDeletedMembers(this
								.getManagedBean().getToBeDelCommitteMemebers());
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
				committeReqDTO.setPayLoad(payLoad);
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO
						.updateFinalizateSecrataryTask(committeReqDTO);

			} else {
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setPayLoad(payLoad);
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				FacesScopes facesScope = new FacesScopes();
				qtReqDTO.setPmUser(facesScope.getUserSessionBean());
				qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
				qtReqDTO.setRoleCode(getUserRoleCode());
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
		debugLogger("retrieveApprovalTabDtl");
		// Added by Fara on 03092014 - Fix for Approval tab if user dint visit
		// the Committee Appointment tab
		List<TabBean> tabList = this.getManagedBean()
				.getScCommitteeMemberTabList();
		if (CollectionUtils.isEmpty(tabList)) {
			retrieveCommitteeAppointmentTabDtl(false);
		}
		this.getManagedBean().setSelectedApprovalTabIndex(0);
		updateQtDtls();
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			ScQt scQt = this.getManagedBean().getQt();

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO resDTO = null;

			reqDTO.setQtdetail(scQt);
			reqDTO.setAvaliableApprovals(scQt.getScQtApprovals());
			reqDTO.setAmendMentBean(this.getManagedBean().getAmendMentBean());
			// reqDTO.setRuleDataBean(this.getManagedBean().getRuleDataBean());
			resDTO = detailSO.retriveQTApprovalDetails(reqDTO);

			List<ScQtApproval> requiredApproval = resDTO.getApproval();

			List<TabBean> tabBeans = new ArrayList<TabBean>();
			List<TabBean> existingApprovalTabs = this.getManagedBean()
					.getTabList();

			ParameterHelper parameterHelper = ParameterHelper.getInstance();
			MenuModel menuModel = new DefaultMenuModel();
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
							if (scQt.getQtStatus().getStatusId() >= QTStatusConst.QT_BRIEFING_SITE_VISIT) {
								tabBeans.add(new TabBean(titleName,
										approvalCategoryCode, qtApproval,
										approverSettingDtls(
												RoleCode.PUBLICATION_APPROVER,
												scQt.getPmOrgProfile()
														.getOrgProfileId(),
												qtApproval)));
							} else {
								if (scQt.getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) {
									this.getManagedBean()
											.setPublicationApproval(qtApproval);
								}
							}
						} else if (ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE
								.equals(approvalCategoryCode)) {
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
							this.getManagedBean().setToBeRemoveList(
									new ArrayList<ScQtApproval>());

						}
						this.getManagedBean().getToBeRemoveList()
								.add(qtApproval);
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
				approvalItem
						.setOncomplete("approvalTabChangeAction([ {name : 'selectedApprovalTabIndex', value : "
								+ approvalTabIndex + "}]);");

				if (ParameterConst.APPROVAL_CATEGORY_OTHERS
						.equals(approvalTabBean.getTabType())) {
					approvalItem.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.qt.approval."
									+ approvalTabBean.getTabType()
											.toLowerCase() + ".tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
				}

				menuModel.addMenuItem(approvalItem);
				approvalTabIndex++;
			}
			this.getManagedBean().setApprovalMenuModel(menuModel);

			// Added by Fara on 03092014 - Fix for Approval tab if no data for
			// approval
			if (menuModel.getContents() != null
					&& menuModel.getContents().size() > 0) {

				this.getManagedBean().setSelectedApprovalTabBean(
						tabBeans.get(this.getManagedBean()
								.getSelectedApprovalTabIndex()));
				this.getManagedBean().setScQtApproval(
						this.getManagedBean().getSelectedApprovalTabBean()
								.getScQtApproval());
				this.getManagedBean().setTabList(tabBeans);
			}
		} finally {
			detailSO = null;
		}
	}

	/**
	 * Retrieve supp qualification tab dtl.
	 */
	public void retrieveSuppQualificationTabDtl() {
		debugLogger("retrieveSuppQualificationTabDtl");
		if (this.getManagedBean().getQt().isZonal()
				&& this.getManagedBean().getQt().isPanel()) {
			retrieveQuationTenderDetails(this.getManagedBean().getQt()
					.getQtId(), QTConstants.DOCTYPE_QT);
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
				if ((this.getManagedBean().getQt().getQtType()
						.equals(QTConstants.OPEN_TYPE) && (null == this
						.getManagedBean().getQtCategoryList() || this
						.getManagedBean().getQtCategoryList().isEmpty()))
						|| (this.getManagedBean().getQt().getQtType()
								.equals(QTConstants.RESTRICTED_TYPE) && (null == this
								.getManagedBean()
								.getSuppCriteriarestrictedList() || this
								.getManagedBean()
								.getSuppCriteriarestrictedList().isEmpty()))) {
					tenderResDTO = QtDetailsSO
							.retriveQTSuppQulCriDetails(tenderReqDTO);
					List<ScQtCategory> categoryResults = tenderResDTO
							.getCategoryResultBean() != null ? tenderResDTO
							.getCategoryResultBean().getResultList() : null;
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

					if (scQt.isPanel()) {
						if (null != scQt.getScQtSuppliers()) {
							this.getManagedBean().setQtSupplierList(
									scQt.getScQtSuppliers());
							this.getManagedBean().setTotalSupplier(
									scQt.getScQtSuppliers().size());
						} else {
							this.getManagedBean().setQtSupplierList(
									new ArrayList<ScQtSupplier>());
						}
					} else if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
							.equalsIgnoreCase(scQt.getQtType())) {
						if (null != tenderResDTO.getSupplierResultBean() && tenderResDTO.getSupplierResultBean()
								.getTotalRecord() > 0) {
							this.getManagedBean().setQtSupplierList(
									tenderResDTO.getSupplierResultBean()
											.getResultList());
							this.getManagedBean().setTotalSupplier(
									tenderResDTO.getSupplierResultBean()
											.getTotalRecord());
						} else {
							this.getManagedBean().setQtSupplierList(
									new ArrayList<ScQtSupplier>());
						}
					}

					boolean isQuotationThresHold = ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt.getProcurementMode().getParameterCode());
					if (!isQuotationThresHold
							&& this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_PENINSULAR
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
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
								this.getManagedBean()
										.setExistingSupplierLocationList(
												locCriteria);
								getManagedBean().getZoneTrees().addAll(
										LocationSingleton.getInstance()
												.constuctTreeNode(locCriteria));
							}
						}
					}
				} else {
					ScQt scQt = this.getManagedBean().getQt();
					boolean isQuotationThresHold = ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt.getProcurementMode().getParameterCode());
					if (!isQuotationThresHold
							&& this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_PENINSULAR
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
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
					
					this.getManagedBean().setPanelItemCategory(
							getParameterDesc(scQt.getScQtSupplierCriteria()
									.getCtPanelItem().getItemCategoryId()));
					List<CtPanelItem> panelList = this.getManagedBean()
							.getSelectedpanelItems();
					panelList = new ArrayList<CtPanelItem>();

					panelList.add(this.getManagedBean().getQt()
							.getScQtSupplierCriteria().getCtPanelItem());
					this.getManagedBean().setSelectedpanelItems(panelList);
				}
				if (null != this.getManagedBean().getQt().getCircularVer()) {
					this.getManagedBean().setCircularExpDate(
							this.getManagedBean().getQt().getCircularVer()
									.getEndDate());
					this.getManagedBean().setCircularNo(
							this.getManagedBean().getQt().getCircularVer()
									.getCircularNo());
					this.getManagedBean().setCircularName(
							this.getManagedBean().getQt().getCircularVer().getName());
					this.getManagedBean().setCircularType(
							this.getManagedBean().getQt().getCircularVer()
									.getDocType());
				}
				if (null != this.getManagedBean().getZoneGeneralTrees()
						&& this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
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
					IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSupplierSO.class);
					QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
					QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
					supplierReqDTO.setPanelItemId(this.getManagedBean()
							.getCtPanelItem().getPanelItemId());
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
					this.getManagedBean().setTotalSuppliers(0);
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
			List<CtPanelItem> itemList = this.getManagedBean()
					.getSelectedpanelItems();
			itemList = new ArrayList<CtPanelItem>();
			itemList.add(this.getManagedBean().getCtPanelItem());
			this.getManagedBean().setSelectedpanelItems(itemList);
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
							if (null != getManagedBean().getQt()
									.getLatestBsvDate()) {
								getManagedBean().getQt().setLatestBsvDate(
										bsvDate);
							}
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
						if (null != getManagedBean().getQt().getLatestBsvDate()) {
							getManagedBean().getQt().setLatestBsvDate(bsvDate);
						}
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

	/**
	 * Retrieve deduction payment tab dtl.
	 */
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

	/**
	 * Retrieve activities tab dtl.
	 */
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

	/**
	 * Retrieve publication approval tab dtl.
	 */
	public void retrievePublicationApprovalTabDtl() {
		debugLogger("retrievePublicationApprovalTabDtl");
		IQTDetailSO detailSO;
		DualListModel<ScQtApproverSettings> dualListModel = null;
		try {
			if (null == this.getManagedBean().getPublicationApproval()) {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				ScQt scQt = getManagedBean().getQt();

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setApprovalType(ParameterConst.APPROVAL_CATEGORY_PTJ);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalDtlsByQtIdNType(reqDTO);
				ScQtApproval publicationApproval = resDTO.getQtApproval();
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
									publicationApproval, scQt
											.getCurrentWorkFlowStatus(),
									publicationApproval
											.getScQtApprovalRemarks(),
									ScQtApprovalRemark.class));

				}
				dualListModel = approverSettingDtls(
						RoleCode.PUBLICATION_APPROVER, scQt.getPmOrgProfile()
								.getOrgProfileId(), publicationApproval);
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
			super(componentId);
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
				populateQTSearchCriteria(qtnTenderSearchBean, searchBean);
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
		FacesScopes facesScopes = new FacesScopes();
		if (facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value())) {
			this.getManagedBean().setMinistryProfile(new PmOrgProfile());
			this.getManagedBean().setPtjProfile(new PmOrgProfile());
		} else if (facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
			this.getManagedBean().setPtjProfile(new PmOrgProfile());
		}
		
	}

	/**
	 * Gets the quote zones.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @param supplierCriteria
	 *            the supplier criteria
	 * @return the quote zones
	 */
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
	public void retrieveQuationTenderDetails(Long qtId, String docType) {
		debugLogger("retrieveQuationTenderDetails");
		// this.getManagedBean().setActiveIndex(0);
		IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setMaintanceFlow(this.getManagedBean()
					.isMaintanceFlow());
			if (null == docType || "".equalsIgnoreCase(docType)) {
				tenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);
			} else {
				tenderReqDTO.setDocType(docType);
			}
			tenderReqDTO.setQtId(qtId);
			tenderReqDTO.setBdgDtlsReq(true);
			QuationTenderResDTO tenderResDTO = null;
			ScQt scQt = this.getManagedBean().getQt();
			
			if (null == scQt
					|| (scQt.isZonal()
					/* && !scQt.isPanel() */
					&& null != this.getManagedBean().getZoneGeneralTrees() && this
							.getManagedBean().getZoneGeneralTrees().isEmpty())) {
				tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
				if (tenderResDTO.getQtDetail() != null) {
					getManagedBean().setQt(tenderResDTO.getQtDetail());
					scQt = this.getManagedBean().getQt();
					Long currentStatus = this.getManagedBean().getQt()
							.getQtStatus().getStatusId();
					this.getManagedBean().setCurrentStatusId(currentStatus);
					getManagedBean().getCtContract().setContractNo(
							this.getManagedBean().getQt()
									.getExistingContractNo());
					//this.getManagedBean().setContractType(scQt.getContractType());
					if (this.getManagedBean().getQt().isZonal()) {
						if (this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
							if (null != this.getManagedBean().getQt()
									.getZoneLocationLevel()) {
								this.getManagedBean().setStateCodes(
										this.getManagedBean().getQt()
												.getZoneLocationLevel()
												.getParameterCode());
							}
							ScQtSupplierCriteria suppCriteria = this.getManagedBean().getQt()
							.getScQtSupplierCriteria();
							if (suppCriteria != null) {
								this.getManagedBean().setSupplierCriteria(
										suppCriteria);
								this.getManagedBean().setCtPanelItem(suppCriteria.getCtPanelItem());
								/*scQt.setPanelItemId(
										suppCriteria.getCtPanelItem().getPanelItemId());*/
								
								circularListSelect(scQt.getCircularVer());
							}
							
							if (tenderResDTO.getQtDetail().isZonal()) {
								this.getManagedBean().setCtzones(Boolean.TRUE);
								if (isNotNullOREmpty(tenderResDTO
										.getZoneDetails())) {
									this.getManagedBean()
											.setTemplateSpecZoneDtls(
													tenderResDTO
															.getZoneDetails());
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
				//this.getManagedBean().setContractType(scQt.getContractType());
			}
		} finally {
			QtDetailsSO = null;
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
						.equals(QTConstants.OPEN_TYPE)) {

			retrieveQuationTenderDetails(this.getManagedBean().getQt()
					.getQtId(), "QT");
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

	/**
	 * Committe tab change action.
	 */
	public void committeTabChangeAction() {
		if (log.isDebugEnabled()) {
			log.debug("Committe Tab change Action");
		}

		Integer selectedCommitteeTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedCommitteeTabIndex"));

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
	public void approvalTabChangeAction() {
		if (log.isDebugEnabled()) {
			log.debug("ApprovalTabChange Action");
		}

		Integer selectedApprovalTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedApprovalTabIndex"));

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
        
		String zonelocationLevel = this.getManagedBean().getStateCodes();
		String locationLevel = this.getManagedBean().getLocalLevelCode();

		boolean pendingRevision = this.getManagedBean().isPendingRevision();

		QuationTenderResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Object payLoad = this.getManagedBean().getPayLoad();
		Long currentStatus = this.getManagedBean().getQt().getQtStatus()
				.getStatusId();
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean()
				.getTemplateSpecZoneDtls();
		QuationTenderReqDTO reqDTO = null;
		switch (activeIndex) {
		case 0:
			reqDTO = prepareQTInfo();
			reqDTO.setSaveFlow(true);
			detailSO.validateateQTDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setTaskName(taskName);
			
			/*this.getManagedBean().setMaintanceFlow(
					this.getManagedBean().getTaskName() == null ? true : false);*/
			
			retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId(), null);
			setAmmendMentBean(resDTO.getAmendMentBean());
			// this.getManagedBean().setQt(resDTO.getQtDetail());
			break;
		case 1:
			reqDTO = prepareQTInfo();
			detailSO.validateSupplierQualificationDtls(reqDTO);
			reqDTO.setSaveFlow(true);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			retrieveSuppQualificationTabDtl();
			setAmmendMentBean(resDTO.getAmendMentBean());
			break;
		case 2:
			reqDTO = prepareQTInfo();
			reqDTO.setSaveFlow(true);
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setScQt(reqDTO.getQtdetail());
			committeReqDTO.setUser(new FacesScopes().getUserSessionBean());
			committeSO.validateAppointCommitteDtls(committeReqDTO);
			// reqDTO = prepareQTInfo();
			resDTO = detailSO.saveQTDtl(reqDTO);
			
			int committeeTabIndex = this.getManagedBean().getSelectedCommitteeTabIndex();
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setSelectedTabIndex(subTabIndex);
			// this.getManagedBean().setActiveIndex(0);
			this.getManagedBean().setSelectedCommitteeTabIndex(committeeTabIndex);
			retrieveCommitteeAppointmentTabDtl(false);
			setAmmendMentBean(resDTO.getAmendMentBean());
			break;
		case 3:
			reqDTO = prepareQTInfo();
			reqDTO.getQtdetail().setModified(false);
			reqDTO.setSaveFlow(true);
			detailSO.validateApprovalDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			QtAmendMentBean amendMentBean = this.getManagedBean()
					.getAmendMentBean();
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			this.getManagedBean().setAmendMentBean(amendMentBean);
			setAmmendMentBean(resDTO.getAmendMentBean());
			retrieveApprovalTabDtl();
			break;
		case 4:
			if (isinvokeValidations(getManagedBean().getQt())) {
				reqDTO = prepareQTInfo();
				detailSO.validateBsvDetails(reqDTO);
				reqDTO.setSaveFlow(true);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				retrieveBriefingSitevisitTabDtl();
			}
			break;
		case 5:
			if (isinvokeValidations(getManagedBean().getQt())) {
				reqDTO = prepareQTInfo();
				detailSO.validateScheduleDetails(reqDTO);
				reqDTO.setSaveFlow(true);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveScheduleTabDtl();
			}
			break;
		case 6:
			if (isinvokeValidations(getManagedBean().getQt())) {
				reqDTO = prepareQTInfo();
				detailSO.validateDeductionDtls(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				reqDTO.setSaveFlow(true);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDeductionPaymentTabDtl();
			}
			break;
		case 7:
			if (isinvokeValidations(getManagedBean().getQt())) {
				reqDTO = prepareQTInfo();
				reqDTO.setSaveFlow(true);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDocumentTabDtl();
				break;
			}
		case 8:
			break;
		case 9:
			if (this.getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				reqDTO.getQtdetail().setModified(true);
				reqDTO.setSaveFlow(true);
				detailSO.validateApprovalDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
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
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		this.getManagedBean().setTaskName(taskName);
		this.getManagedBean().setPendingRevision(pendingRevision);
		this.getManagedBean().setPayLoad(payLoad);
		this.getManagedBean().setCreateQt(isCreateQt);
		if (isCreateQt) {
			this.getManagedBean().setOutComes(
					new String[] { ResourceBundleUtils.getMessage("lbl.btn.submit",
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
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = prepareQTInfo();
		boolean isAuditor = this.isAuditor();
		if (priviousIndex != activeIndex) {
		 if(!isAuditor){
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

				detailSO.validateApprovalDetails(reqDTO);
				// retrieveApprovalTabDtl();
				break;

			case 4:
				if (isinvokeValidations(getManagedBean().getQt())) {
					detailSO.validateBsvDetails(reqDTO);
				}
				break;

			case 5:
				if (!this.getManagedBean().isMaintanceFlow()) {
					if (getManagedBean().getQt().getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT
							&& getManagedBean().getQt().getQtStatus()
									.getStatusId() < QTStatusConst.QT_BRIEFING_SITE_VISIT) {

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
					if (getManagedBean().getQt().getQtStatus().getStatusId() >= QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT
							&& getManagedBean().getQt().getQtStatus()
									.getStatusId() < QTStatusConst.QT_BRIEFING_SITE_VISIT) {

						reqDTO.getQtdetail().setCallPTJValidation(true);
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
						.getQtId(), null);
				break;
			case 1:
				retrieveSuppQualificationTabDtl();
				break;

			case 2:
				// detailSO.validateSupplierQualificationDtls(reqDTO);
				retrieveCommitteeAppointmentTabDtl(false);
				break;
			case 3:
				// detailSO.validateApprovalDetails(reqDTO);
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
				if (!briefings.isEmpty()) {
					bsvDetils.addAll(briefings);
				}
				if (!siteVisit.isEmpty()) {
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
				if (!briefings.isEmpty()) {
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
	 * method to give whether to enable buttons or not..
	 * 
	 * @param statusId
	 *            the status id
	 * @param evalType
	 *            the eval type
	 * @param committe
	 *            the committe
	 * @return the enable flag by status
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

		if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(evalType)) {
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
	 * 
	 * @return the string
	 */
	public String submitQtDetailsFormMaintanace() {

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

			if (this.getManagedBean().getCurrentStatusId() >= QTStatusConst.QT_PENDING_PUBLICATION) {
				quationTenderReqDTO.setQtId(scQt.getQtId());
				quationTenderReqDTO
						.setApprovalType(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalsByQtIdNType(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQtApproval qtApproval = resDTO.getQtApproval();

				qtApproval.setScQtApprovalRemarks(initializeRemarks(qtApproval,
						this.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(),
						qtApproval.getScQtApprovalRemarks(),
						ScQtApprovalRemark.class));
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
	public void changePbSecretaryCommType(AjaxBehaviorEvent event) {
		debugLogger("retrieveFinalizationCommitteeSecretaryName");
		String commType = (String) event.getComponent().getAttributes()
				.get("value");
		if (null != commType) {
			getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
					.getApproverSettings().get(0).setApprover(new PmUser());
		}
	}

	/**
	 * Constuct condition fields.
	 * 
	 * @return the string
	 */
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

	/**
	 * Gets the latest bsv date.
	 * 
	 * @param bsvDetails
	 *            the bsv details
	 * @return the latest bsv date
	 */
	private Date getLatestBsvDate(List<ScQtBsvDtl> bsvDetails) {
		List<Date> dates = new ArrayList<Date>();
		Date latestBsvDate = null;
		for (ScQtBsvDtl bsvDtl : bsvDetails) {
			if (bsvDtl.getBsvDate() == null) {
				throw new EpException(MessageConst.ERROR_760156);
			} else {
				dates.add(bsvDtl.getBsvDate());
			}
		}
		if (null != dates && !dates.isEmpty()) {
			latestBsvDate = Collections.max(dates);
		}
		return latestBsvDate;
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
			if (QTStatusConst.QT_BRIEFING_SITE_VISIT <= qt.getQtStatus()
					.getStatusId()) {
				qt.setScQtCommittees(null);
				qt.setScQtBriefings(null);
				qt.setScQtStatus(null);

				ScQtApproval approval = this.getManagedBean()
						.getSelectedApprovalTabBean().getScQtApproval();
				finalizeRemarks(approval.getScQtApprovalRemarks());

				List<ScQtApproverSettings> approverSettings = approval
						.getApproverSettings();
				if (null != approverSettings && !approverSettings.isEmpty()) {
					String fcCommType = approverSettings.get(0)
							.getCommitteType();
					if (StringUtils.isEmpty(fcCommType)) {
						throw new EpException(
								MessageConst.ERROR_100022,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.sc.finalization.committee",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) });
					}

					PmUser approver = approverSettings.get(0).getApprover();
					if (approver == null || approver.getUserId() == null) {
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

				List<ScQtApproval> scQtApprovals = new ArrayList<ScQtApproval>();

				scQtApprovals.add(approval);
				qt.setScQtApprovals(scQtApprovals);
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setPublished(true);
				quationTenderReqDTO.setPmUser(new FacesScopes()
						.getUserSessionBean());
				quationTenderReqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
			} else {
				quationTenderReqDTO.setQtId(this.getManagedBean().getQt()
						.getQtId());
				QuationTenderResDTO resDTO1 = detailSO
						.retriveQtById(quationTenderReqDTO);
				ScQt scQt = resDTO1.getQtDetail();
				ScQtStatus qtStatus = new ScQtStatus();
				qtStatus.setScQt(scQt);
				PmStatus pmStatus = new PmStatus();
				pmStatus.setStatusId(QTStatusConst.QT_CANCELLED);
				qtStatus.setPmStatus(pmStatus);
				qtStatus.setCurrent(Boolean.TRUE);
				qtStatus.setDocType(QTConstants.DOCTYPE_QT);

				
				for (ScQtStatus status : scQt.getScQtStatus()) {
					status.setCurrent(Boolean.FALSE);
				}
				scQt.getScQtStatus().add(qtStatus);
				finalizeRemarks(qt.getScQtRemarks());
				if(CollectionUtils.isEmpty(qt.getScQtRemarks())){
					qt.setScQtRemarks(initializeRemarks(qt, qt.getCurrentWorkFlowStatus(),
							qt.getScQtRemarks(), ScQtRemark.class));
					throw new EpException(
							MessageConst.ERROR_100022,
							new String[] {" Remark" });
					
				}
				/*
				 * List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
				 * qtStatuses.add(qtStatus);
				 */
				qt.setScQtStatus(scQt.getScQtStatus());
				qt.setScQtCommittees(null);
				qt.setScQtApprovals(null);
				qt.setScQtBriefings(null);
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setPublished(false);
				quationTenderReqDTO.setPmUser(new FacesScopes()
						.getUserSessionBean());

			}
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
													MessageConst.INFO_700031,
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
						.setApprover(user);
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

		return enableFieldByStatusNFiledName(fieldName, statusId, this
				.getManagedBean().isMaintanceFlow(), isSourcingDeskOfficer());

	}

	/**
	 * Update qt ammend ment dtls.
	 * 
	 * @param componentId
	 *            the component id
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	private void updateQtAmmendMentDtls(String componentId, Object oldValue,
			Object newValue) {
		QtAmendMentBean amendMentBean = this.getManagedBean()
				.getAmendMentBean();
		if (null == amendMentBean) {
			amendMentBean = new QtAmendMentBean();
		}
		if ("title1".equalsIgnoreCase(componentId)) {

			if (null == amendMentBean.getOldTitle()) {
				amendMentBean.setOldTitle((String) oldValue);

			}

			amendMentBean.setNewTitle((String) newValue);

		} else if ("estprice".equalsIgnoreCase(componentId)) {

			if (null == amendMentBean.getOldEstmtPrice()) {
				amendMentBean.setOldEstmtPrice((BigDecimal) oldValue);

			}
			amendMentBean.setNewEstmtPrice((BigDecimal) newValue);
			if (!amendMentBean.getOldEstmtPrice().equals(
					amendMentBean.getNewEstmtPrice())) {
				amendMentBean.setPTJApprovalReq(true);
			}
		} else if ("proposalvalidity".equalsIgnoreCase(componentId)) {

			if (0 == amendMentBean.getOldValidityPeriodDays()) {
				amendMentBean.setOldValidityPeriodDays((Integer) oldValue);

			}
			if (amendMentBean.getOldValidityPeriodDays() != amendMentBean
					.getNewValidityPeriodDays()) {

			}
			amendMentBean.setNewValidityPeriodDays((Integer) newValue);

		} else if ("publishperiod".equalsIgnoreCase(componentId)) {

			if (0 == amendMentBean.getOldPublishPeriodDays()) {
				amendMentBean.setOldPublishPeriodDays((Integer) oldValue);

			}

			amendMentBean.setNewPublishPeriodDays((Integer) newValue);
			if (amendMentBean.getOldPublishPeriodDays() != amendMentBean
					.getNewPublishPeriodDays()) {
				amendMentBean.setPTJApprovalReq(true);
			}
		}

		this.getManagedBean().setAmendMentBean(amendMentBean);
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
			Long qtStatus = pmStatus.getStatusId();
			if (qtStatus > QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING
					&& qtStatus < QTStatusConst.QT_BRIEFING_SITE_VISIT
					&& !this.getManagedBean().isMaintanceFlow()) {
				return true;
			}
		}
		return false;

	}

	public void publishPeriodDaysChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(),
				changeEvent.getOldValue(), changeEvent.getNewValue());

	}

	public void estimateAmtChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(),
				changeEvent.getOldValue(), changeEvent.getNewValue());

	}

	public void proposalValidityChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(),
				changeEvent.getOldValue(), changeEvent.getNewValue());

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

			if (QTStatusConst.QT_BRIEFING_SITE_VISIT.compareTo(statusId) > 0) {

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
		if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(this
				.getManagedBean().getQt().getQtStatus().getStatusId())
				&& (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode()) || (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode())))) {
				return (!this.getManagedBean().isDeskOfficer());
		}
		List<ScQtApprovalRequest> approvalRequests = approval
				.getScQtApprovalDtls();
		if (CollectionUtils.isNotEmpty(approvalRequests)) {

			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				if (null == approvalRequest.getSubmissionDate()
						|| (null != approvalRequest.getApproverAction() && "V"
								.equalsIgnoreCase(approvalRequest
										.getApproverAction().getParameterCode()))) {
						return (!this.getManagedBean().isDeskOfficer());
				}
			}
		}
		return true;
	}

	 /**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		debugLogger("getPTJCondition");
		String condition = null;
		if (null != this.getManagedBean().getMinistryProfile()) {
			condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
					this.getManagedBean().getMinistryProfile().getOrgProfileId();
		} else {
			condition = "";
		}
		return condition;
	}
	
	/**
	* The method used reset PTJ profile value once select the Ministry
	*/
	public void resetPtjProfile() {
		debugLogger("resetPtjProfile");
		getManagedBean().setPtjProfile(null);
	}
	
	public boolean disableMinistryLov() {
		debugLogger("disableMinistryLov");
		return (isSourcingDeskOfficer() || getFacesScopes()
				.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()));
	}

	public boolean disablePtjLov() {
		debugLogger("disablePtjLov");
		return (isSourcingDeskOfficer() || getManagedBean().getMinistryProfile()==null);
	}
	
	/**
	 * 
	 *
	 * @return true
	 */
	public boolean isSourcingDeskOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value());
	}
	
	/**
	 * Checks if is Auditor General/Internal user.
	 *
	 * @return true, if is Auditor General/Internal user
	 */
	public boolean isAuditor() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value()) || facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value());
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
									&& "V".equalsIgnoreCase(approvalAction
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

	public void qtMaiantanceValueChangeListner(ValueChangeEvent event) {

		updateQtAmmendMentDtls(event.getComponent().getId(),
				event.getOldValue(), event.getNewValue());

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

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.info("handleFileUpload - START");
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
			if (scQtApproval.getScQt() == null) {
				scQtApproval.setScQt(this.getManagedBean().getQt());
			}
			List<ScQtApprovalAttachment> approvalAttachments = scQtApproval
					.getScQtApprovalAttachments();
			if (null == approvalAttachments) {
				approvalAttachments = new ArrayList<ScQtApprovalAttachment>();
			}
			attachment.setScQtApproval(scQtApproval);
			attachment.setDocNo(scQtApproval.getScQt().getQtNo());
			approvalAttachments.add(attachment);
			scQtApproval.setScQtApprovalAttachments(approvalAttachments);
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			getManagedBean().setScQtApproval(scQtApproval);
			attachments.add(attachment);
			getManagedBean().setAttachments(attachments);
		}
		log.info("handleFileUpload - END");
	}
     
	/**
	 * Set required data for Tracking Dairy info of QT
	 * 
	 * @param scQt
	 */
	private void setScQtTrackingDairyObject(ScQt scQt){
		
		Object[] scTdObj = new Object[7];
		scTdObj[0] = scQt.getQtId();
		scTdObj[1] = scQt.getQtNo();
		scTdObj[2] = scQt.getCreatedDate();
		scTdObj[3] = ParameterConst.DOCTYPE_QUOTATION;
		scTdObj[4] = scQt.getQtStatus().getStatusId();
		scTdObj[5] = scQt.getQtTitle();
		this.getManagedBean().setScTdObj(scTdObj);
	}
	/*
	 * private void setEstimatedPrice(ScQt scQt) {
	 * 
	 * IQTDetailSO qtDetailsSO; try { qtDetailsSO =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	 * 
	 * BigDecimal estimatedAmount = qtDetailsSO.getdecryptedEstAmount(scQt
	 * .getQtId()); scQt.setEstimatedAmount(estimatedAmount);
	 * scQt.setDecryptedEstAmt(estimatedAmount); } finally { qtDetailsSO = null;
	 * } }
	 */
	
	private void circularListSelect(CtCircularVer circular) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			this.getManagedBean().getQt().setCircularVer(circular);
			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICircularDetailSO.class);
			if (null != circular) {
				reqDTO.setCircularVerId(circular.getCircularVerId());
				reqDTO.setQuotationTender(true);
				CircularDetailResDTO resDTO = circularDetailSO
						.getPanelItemsByCircularVerId(reqDTO);
				this.getManagedBean().setSelectedpanelItems(
						resDTO.getPanelItemList());
				this.getManagedBean().setCircularType(circular.getDocType());

			}

		} finally {

		}
	}
}
