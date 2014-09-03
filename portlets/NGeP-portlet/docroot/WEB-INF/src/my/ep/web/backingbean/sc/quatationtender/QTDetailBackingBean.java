package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.Calendar;
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
import javax.servlet.http.HttpServletRequest;

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
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
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
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * @Author VidyasagarReddy
 */
@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);

	private FacesContext facesContext = FacesContext.getCurrentInstance();

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
	 * Onchange of gos to fetch the so's list
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
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setDocType(QTConstants.DOCTYPE_QT);
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			ScQt selectedScQt = resDTO.getQtDetail();
			this.getManagedBean().setQt(selectedScQt);
			if (QTStatusConst.QT_PENDING_APPOINT_BO.equals(selectedScQt
					.getQtStatus().getStatusId())
					|| QTStatusConst.QT_PENDING_SPECIFICATION_PREPARATION
							.equals(selectedScQt.getQtStatus().getStatusId())
					|| (QTStatusConst.QT_CANCELLED.equals(selectedScQt
							.getQtStatus().getStatusId()) && null == selectedScQt
							.getQtType())) {
				ScQtCommittee committee = getQtCommiteDtlsByQtIdNType("BO",
						selectedScQt.getQtId());
				List<ScQtCommittee> qtCommittee = new ArrayList<ScQtCommittee>();
				qtCommittee.add(committee);
				selectedScQt.setScQtCommittees(qtCommittee);
				this.getManagedBean().setScQtCommittee(committee);
				this.getManagedBean().setScCommitteeMemberList(
						committee.getScCommittee().getScCommitteeMembers());
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
				this.getManagedBean()
						.setCommitteMember(new ScCommitteeMember());
				REDIRECT_PAGE = QTConstants.BO_MAINTANANCE_PAGE;
			} else {
				getManagedBean().setQt(resDTO.getQtDetail());
				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (resDTO.getQtDetail().isZonal()) {
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
				// selectedScQt.setFileNo(scQt.getQtNo());
				this.getManagedBean().setQt(selectedScQt);
				if (getManagedBean().getQt().getEstimatedAmount() != null) {
					if (getManagedBean().getQt().getEstimatedAmount()
							.intValue() > SourcingConstants.ESTMT_TWANTY_THOUSAND
							&& getManagedBean().getQt().getEstimatedAmount()
									.intValue() < SourcingConstants.ESTMT_FIFTY_THOUSAND) {
						if (getManagedBean().getQt().getQtValidityPeriod() == null) {
							getManagedBean().getQt().setQtValidityPeriod(
									SourcingConstants.SIXTY_DAYS);
						}
						if (getManagedBean().getQt().getPublishPeriod() == null) {
							getManagedBean().getQt().setPublishPeriod(
									SourcingConstants.PUBLISH_THREE_DAYS);
						}
					} else if (getManagedBean().getQt().getEstimatedAmount()
							.intValue() > SourcingConstants.ESTMT_FIFTY_THOUSAND
							&& getManagedBean().getQt().getEstimatedAmount()
									.intValue() < SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
						if (getManagedBean().getQt().getQtValidityPeriod() == null) {
							getManagedBean().getQt().setQtValidityPeriod(
									SourcingConstants.NINTY_DAYS);
						}
						if (getManagedBean().getQt().getPublishPeriod() == null) {
							getManagedBean().getQt().setPublishPeriod(
									SourcingConstants.PUBLISH_SEVEN_DAYS);
						}
					} else if (getManagedBean().getQt().getEstimatedAmount()
							.intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
						if (getManagedBean().getQt().getQtValidityPeriod() == null) {
							getManagedBean().getQt().setQtValidityPeriod(
									SourcingConstants.NINTY_DAYS);
						}
						if (getManagedBean().getQt().getPublishPeriod() == null) {
							getManagedBean().getQt().setPublishPeriod(
									SourcingConstants.PUBLISH_TWENTYONE_DAYS);
						}
					}
				}
			}
		} finally {
			detailSO = null;
		}
		return REDIRECT_PAGE + "?faces-redirect=true";
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
		
		scQt.setExistingContractNo(this.getManagedBean().getCtContract().getContractNo());

		// No need to set the below fields automatically set by listener
		PmUser users = new FacesScopes().getUserSessionBean();
		scQt.setCreatedBy(users.getUserId());
		scQt.setChangedBy(users.getUserId());
		// Setting the pm status for qt details
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(1l);
		// Setting the Committee Appointment details to Qtdetails
		List<TabBean> committeTabs = getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommitteList = null;
		if (null != committeTabs) {
			qtCommitteList = new ArrayList<ScQtCommittee>();
			for (TabBean tabBean : committeTabs) {
				if (QTConstants.FINALIZATION_SECRETARY_TAB
						.equalsIgnoreCase(tabBean.getTitleName())) {

				} else {
					List<ScCommitteeMember> committeMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					if (!"BO".equals(tabBean.getScQtCommittee()
							.getScCommittee().getCommitteeType()
							.getParameterCode())) {
						if (null != committeMembers
								&& !committeMembers.isEmpty()) {
							for (int i = 0; i < committeMembers.size(); i++) {
								committeMembers.get(i).setScCommittee(
										tabBean.getScQtCommittee()
												.getScCommittee());
							}

							if (QTStatusConst.CM_PENDING_SUBMISSION
									.equals(tabBean.getScQtCommittee()
											.getScCommittee()
											.getCommitteStatus().getStatusId())) {
								PmStatus committeStatus = new PmStatus();
								committeStatus
										.setStatusId(QTStatusConst.CM_PENDING_APPROVAL);
								tabBean.getScQtCommittee().getScCommittee()
										.setCommitteStatus(committeStatus);
							}
							qtCommitteList.add(tabBean.getScQtCommittee());
						}
					}
				}

			}
		}
		scQt.setScQtCommittees(qtCommitteList);
		// Code for to update the supplier qualification criteria
		List<ScQtCategory> categoryList = new ArrayList<ScQtCategory>();
		if (!this.getManagedBean().getQt().isPanel()) {
			if (this.getManagedBean().getQt().getQtType()
					.equals(QTConstants.OPEN_TYPE)) {
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (openList != null && !openList.isEmpty()) {
					for (ScQtCategory scQtCategory : openList) {
						scQtCategory.setPmCategoryL1(scQtCategory
								.getPmCategoryL3().getPmCategoryL2()
								.getPmCategoryL1());
						scQtCategory.setPmCategoryL2(scQtCategory
								.getPmCategoryL3().getPmCategoryL2());
						scQtCategory.setScQt(this.getManagedBean().getQt());
						setUserDetails(scQtCategory);
						categoryList.add(scQtCategory);
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
						scQtCategory.setScQt(this.getManagedBean().getQt());
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
		if (!scQt.isZonal() && !scQt.isPanel()) {
			scQt.setZoneLocationLevel(null);
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), null);
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
			List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
					this.getManagedBean().getZoneGeneralTrees(), null);
			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
				if (null != this.getManagedBean().getSupplierCriteria()) {
					ScQtZoneCriteria zoneCriteria = this.getManagedBean()
							.getSupplierCriteria().getQtZoneCriterias();
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
						zoneLocal.add(tplSpecZoneDtl);
						zoneCriteria.setTplSpecZoneDtls(zoneLocal);
						zoneCriteria.setScQtSupplierCriteria(this
								.getManagedBean().getSupplierCriteria());
						setUserDetails(zoneCriteria);
						this.getManagedBean().getSupplierCriteria()
								.setQtZoneCriterias(zoneCriteria);
					}
					this.getManagedBean().getSupplierCriteria()
							.setScQtLocCriteria(null);
					this.getManagedBean().getSupplierCriteria()
							.setLocationLevel(scQt.getZoneLocationLevel());
				}
			}
		}
		if (null != this.getManagedBean().getSupplierCriteria()) {
			this.getManagedBean().getSupplierCriteria()
					.setScQt(this.getManagedBean().getQt());
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
				tabBean.getScQtApproval().setScQtApprovalRemarks(
						finalizeRemarks(tabBean.getScQtApproval()
								.getScQtApprovalRemarks()));
				// setUserDetails(tabBean.getScQtApproval());
				tabBean.getScQtApproval()
						.setScQt(this.getManagedBean().getQt());
				approvalList.add(tabBean.getScQtApproval());

			}
		}
		scQt.setScQtApprovals(approvalList);
		scQt.setScQtBriefings(null);
		if (scQt.getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
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
					scQtBsv.setScQt(this.getManagedBean().getQt());
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
				}

			} else {
				// code for if bsv details if any and non zonal
				BriefingZoneBean bsvDtlBean = this.getManagedBean()
						.getBsvDtlBean();
				ScQtBsv scQtBsv = constructBsvByBean(bsvDtlBean);

				if (null != scQtBsv) {
					scQtBsv.setBsvRemarks(finalizeRemarks(scQtBsv
							.getBsvRemarks()));
					scQtBsv.setScQt(this.getManagedBean().getQt());
					bsvs = new ArrayList<ScQtBsv>();
					bsvs.add(scQtBsv);
					scQt.setBsvRequired(scQtBsv.getScQtBsvDtls().size() > 0 ? true
							: false);
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
						scQtSampleSubmission.getAddress().setPmCountry(retrieveCountry());
						scQtSampleSubmission.setScQt(getManagedBean().getQt());
					}

				}
			} else {
				sampleSubmissionList = new ArrayList<ScQtSampleSubmission>();
				if (null != this.getManagedBean().getScQtSampleSubmission()) {
					this.getManagedBean().getScQtSampleSubmission().getAddress().setPmCountry(retrieveCountry());
					sampleSubmissionList.add(this.getManagedBean()
							.getScQtSampleSubmission());
				}
			}
			scQt.setScQtSampleSubmission(sampleSubmissionList);
			// Setting the contact person to qtdetails.
			// TODO Need to add for duplications..
			List<ScQtContactPerson> list = new ArrayList<ScQtContactPerson>();
			List<ScQtContactPerson> contactPersonsList = this.getManagedBean()
					.getScQtContactPersons();
			List<ScQtContactPerson> deskOfficerAdminList = this
					.getManagedBean().getPmuserContactpersonList();
			if (contactPersonsList != null && !contactPersonsList.isEmpty()) {
				list.addAll(contactPersonsList);
			}
			if (deskOfficerAdminList != null && !deskOfficerAdminList.isEmpty()) {
				list.addAll(deskOfficerAdminList);
			}
			if (list != null && !list.isEmpty()) {
				scQt.setScQtContactPersons(list);
			}
			// Setting the Deductions to qtdetails.
			List<ScQtDeduction> deductionList = getManagedBean()
					.getScQtDeductionList();
			if (null != deductionList && !deductionList.isEmpty()) {
				for (ScQtDeduction deduction : deductionList) {
					List<ScQtDeductionClause> deductionClauses = deduction
							.getScQtDeductionClauses();
					if (null != deductionClauses && !deductionClauses.isEmpty()) {
						for (ScQtDeductionClause deductionClause : deductionClauses) {
							deductionClause.setScQtDeduction(deduction);

						}
						deduction.setScQtDeductionClauses(deductionClauses);
					}
				}

			}
			scQt.setScQtDeductions(deductionList);

			// Setting the Documents to qtdetails.

			List<ScQtDocReport> scQtDocReportsList = null;
			List<ScQtDocReport> docReportsList = getManagedBean()
					.getScQtDocReportList();
			if (docReportsList != null && !docReportsList.isEmpty()) {
				scQtDocReportsList = new ArrayList<ScQtDocReport>();
				scQtDocReportsList.addAll(docReportsList);

				// for (int i = 0; i < docReportsList.size(); i++) {
				for (int j = 0; j < scQtDocReportsList.size(); j++) {
					if (scQt.getFulfilmentType().getParameterCode()
							.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
							&& scQt.getScheduleType().equals(
									SourcingConstants.FREQUENCY_SCHEDULE)) {
						// Schedule
						if (null != scQtDocReportsList.get(j).getPmDocument())
							if (scQtDocReportsList
									.get(j)
									.getPmDocument()
									.getDocDefinitionKey()
									.equalsIgnoreCase(
											SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE)) {
								scQtDocReportsList.remove(j);
								break;
							}
					} else if (scQt.getFulfilmentType().getParameterCode()
							.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
							&& scQt.getScheduleType().equals(
									SourcingConstants.PROGRESSIVE_PAYMENT)) {
						// Progressive
						if (null != scQtDocReportsList.get(j).getPmDocument())
							if (scQtDocReportsList
									.get(j)
									.getPmDocument()
									.getDocDefinitionKey()
									.equalsIgnoreCase(
											SourcingConstants.DOC_DEF_KEY_PROGRESSIVE_PAYMENT)) {
								scQtDocReportsList.remove(j);
								break;
							}
					} else if (scQt.getFulfilmentType().getParameterCode()
							.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
						// Progressive
						if (null != scQtDocReportsList.get(j).getPmDocument())
							if (scQtDocReportsList
									.get(j)
									.getPmDocument()
									.getDocDefinitionKey()
									.equalsIgnoreCase(
											SourcingConstants.DOC_DEF_KEY_PROGRESSIVE_PAYMENT)) {
								scQtDocReportsList.remove(j);
								break;
							}
					}
				}
				// }
			}
			scQt.setScQtDocReports(scQtDocReportsList);
			// Setting the Publication Approvals to QtDetails
			ScQtApproval approval = getManagedBean().getPublicationApproval();
			if (null != approval) {
				if (null == approvalList) {
					approvalList = new ArrayList<ScQtApproval>();
				}
				List<ScQtApprovalRequest> approvalDtls = new ArrayList<ScQtApprovalRequest>();
				ScQtApprovalRequest scQtApprovalDtl = new ScQtApprovalRequest();
				ParameterReqDTO reqDTO1 = new ParameterReqDTO();
				reqDTO1.setParameterCode(ParameterConst.APPROVAL_CATEGORY_PTJ);
				reqDTO1.setParameterType(ParameterConst.APPROVAL_CATEGORY_TYPE);
				scQtApprovalDtl.setApprovalType(getParameterSO()
						.retrieveParameterByTypeAndCode(reqDTO1)
						.getPmParameter());
				scQtApprovalDtl.setScQtApproval(approval);
				approvalDtls.add(scQtApprovalDtl);
				approval.setScQtApprovalDtls(approvalDtls);
				ParameterReqDTO reqDTO2 = new ParameterReqDTO();
				reqDTO2.setParameterCode(ParameterConst.APPROVAL_CATEGORY_PTJ);
				reqDTO2.setParameterType(ParameterConst.APPROVAL_CATEGORY_TYPE);
				approval.setApprovalCategory(getParameterSO()
						.retrieveParameterByTypeAndCode(reqDTO2)
						.getPmParameter());
				approval.setApproverSettings(this.getManagedBean()
						.getPublicationApprovals().getTarget());
				List<ScQtApprovalRemark> remarks = approval
						.getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						if (remarks.get(i).getRemark() == null
								|| remarks.get(i).getRemark() == "") {
							approval.getScQtApprovalRemarks().remove(i);
						}
					}
				}
				approval.setScQt(this.getManagedBean().getQt());
				approvalList.add(approval);
			}
			scQt.setScQtApprovals(approvalList);

		}
		scQt.setScQtStatus(null);
		// save.
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);
		quationTenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);
		return quationTenderReqDTO;
	}

	/**
	 * @return
	 */
	private PmCountry retrieveCountry() {
		ICountrySO countrySo = null;
		PmCountry country  = null;
		try {
			countrySo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
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
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromCreatedDate, toCreatedDate);
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
			specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto.getFinSpecResultBean().getResultList() != null && !resDto.getFinSpecResultBean().getResultList().isEmpty()) {
				this.getManagedBean().setFinancialSpecList(resDto.getFinSpecResultBean().getResultList());
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
			specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto.getSpecResultBean().getResultList() != null && !resDto.getSpecResultBean().getResultList().isEmpty()) {
				this.getManagedBean().setTechnicalSpecList(resDto.getSpecResultBean().getResultList());
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
				reqDTO.setQtId(getManagedBean().getQt().getQtId());
				reqDTO.setLanguageCode(languageCode);
				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingSiteVisitDtl(reqDTO);
				// getting Briefing Same As Site Visit value
				ScQt scQt = resDTO.getScQt();
				// getManagedBean().setScQt(scQt);
				if (scQt.isZonal()) {
					List<TplSpecZoneDtl> zones = resDTO.getZonesList();
					List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
					if (null != zones) {
						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
						briefingReqDTO.setQtId(getManagedBean().getQt()
								.getQtId());
						for (TplSpecZoneDtl specZoneDtl : zones) {
							briefingReqDTO.setSpecZoneId(specZoneDtl
									.getSpecZoneId());
							BriefingResDTO briefingResDTO = qTBriefingSO
									.retrieveBriefingSiteVisitDtls(briefingReqDTO);
							ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
							scQtBsv.setBsvRemarks(initializeRemarks(scQtBsv, scQtBsv.getBsvRemarks(),
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
					qtBsv.setBsvRemarks(initializeRemarks(qtBsv, qtBsv.getBsvRemarks(),
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
		Boolean id = (Boolean) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
	}

	public void retrieveCommitteeAppointmentTabDtl() {
		debugLogger("retrieveCommitteeAppointmentTabDtl");
		this.getManagedBean().getQt().setFinalizationCommitte(new PmParameter());
		this.getManagedBean().getQt().setFinalizationSecratery(new PmUser());
		MenuModel menuModel = new DefaultMenuModel();
		List<TabBean> committeTabs = this.getManagedBean().getScCommitteeMemberTabList();
		Long qtId = getManagedBean().getQt().getQtId();
		if (null != qtId && null == committeTabs) {
			String evaluationType = this.getManagedBean().getQt().getEvaluationType();
			committeTabs = new ArrayList<TabBean>();
			// BO and specification committe is common for both evaluations..
			committeTabs.add(new TabBean(QTConstants.BUSINESS_OWNER, getQtCommiteDtlsByQtIdNType("BO", qtId)));
			committeTabs.add(new TabBean(QTConstants.SPECIFICATION_COMMITTEE_TAB, getQtCommiteDtlsByQtIdNType(CommitteType.getValue("SpecificationCommittee"), qtId)));
			// evaluation type is one tier only oc is added..
			if ("1".equals(evaluationType)) {
				committeTabs.add(new TabBean("Evaluation Committee", getQtCommiteDtlsByQtIdNType(CommitteType.getValue("EvaluationCommittee"), qtId)));
			}
			// evaluation type is two tier below committe are added
			else {
				committeTabs.add(new TabBean(QTConstants.OPENING_COMMITTEE_TAB, getQtCommiteDtlsByQtIdNType(CommitteType.getValue("OpeningCommittee"), qtId)));
				committeTabs.add(new TabBean("TEC", getQtCommiteDtlsByQtIdNType("TEC", qtId)));
				committeTabs.add(new TabBean("FEC", getQtCommiteDtlsByQtIdNType("FEC", qtId)));
			}

			ScQtCommittee qtCommittee = new ScQtCommittee();
			this.getManagedBean().getQt().setFinalizationCommitte(new PmParameter());
			this.getManagedBean().getQt().setFinalizationSecratery(new PmUser());
			qtCommittee.setScQt(this.getManagedBean().getQt());
			committeTabs.add(new TabBean(QTConstants.FINALIZATION_SECRETARY_TAB, qtCommittee));
			
			this.getManagedBean().setScCommitteeMemberTabList(committeTabs);

			int zoneTabIndex = 0;
			for (TabBean tabBean : committeTabs) {
				MenuItem committeItem = new MenuItem();
				committeItem.setValue(tabBean.getTitleName());
				committeItem.setUpdate("@form");
				if (!QTConstants.FINALIZATION_SECRETARY_TAB.equalsIgnoreCase(tabBean.getTitleName())) {
					if (tabBean.getScQtCommittee().getScCommittee().getCommitteStatus().getStatusId().equals(QTStatusConst.CM_COMPLETE)) {
						committeItem.setIcon("ui-icon-endorse");
					}
				}
				committeItem.setActionExpression(FacesScopes.createMethodExpression(facesContext, "#{qTDetailBB.committeTabChangeAction(" + zoneTabIndex + ")}", null, new Class[] { Integer.class }));
				menuModel.addMenuItem(committeItem);
				zoneTabIndex++;
			}
			getManagedBean().setMenuModel(menuModel);
			if (committeTabs.size() > 0) {
				getManagedBean().setSelectedTabBean(committeTabs.get(0));
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
			committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveFinalizationSecratryDtls(reqDTO);
		} finally {
			committeSO = null;
		}
		return resDTO.getFinalizationSecratry();
	}

	/**
	 * @param string
	 * @param object
	 * @return
	 */
	private ScQtCommittee getFinalizationCommitteeTypeDtl(String commType,
			Long id) {
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		ParameterResDTO parameterResDTO = null;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		// ScCommittee committee = null;
		reqDTO.setCommitteeType(commType);
		reqDTO.setFinalizationSecratary(true);
		CommitteResDTO resDTO = null;
		ScQtCommittee qtCommitte = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
			if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			}
			if (null == qtCommitte) {
				qtCommitte = new ScQtCommittee();
				qtCommitte.setScQt(this.getManagedBean().getQt());
				setUserDetails(qtCommitte);
				qtCommitte.setCommitteeActivities(null);
				qtCommitte.setQtCommitteRemarks(null);
				qtCommitte.setScQtMeetings(null);
				ScCommittee committee = new ScCommittee();
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				setUserDetails(committee);
				PmStatus committeStatus = new PmStatus();
				committeStatus.setStatusId(60200l);
				committee.setCommitteStatus(committeStatus);
				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
				qtCommitte.setScCommittee(committee);
			}
		} finally {
			committeSO = null;
		}
		return qtCommitte;
	}

	public void retrieveBusinessOwnerTabDtl() {
		debugLogger("retrieveBusinessOwnerTabDtl");
		if (getManagedBean().getQt().getQtId() != null) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
				List<ScCommitteeMember> committeeMembersList = resDTO.getCommMemberResultBean().getResultList();
				getManagedBean().setScCommitteeMemberList(committeeMembersList);
			} finally {
				committeSO = null;
			}
		}
	}

	public void preRenderView() {
		renewSesionObject();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		log.info("taskId : " + taskId);
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
		String taskName = res.getTaskName();
		this.getManagedBean().setOutComes(res.getAction());
		Long qtId = null;

		if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
						.equalsIgnoreCase(taskName)) {
			if (!SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
					.equalsIgnoreCase(taskName)) {
				qtId = ((SCCommitteeDataType) obj).getQtId().longValue();
			} else {
				qtId = ((SCEvaluationDataType) obj).getQtId().longValue();

			}
			this.getManagedBean().setActiveIndex(1);
			retrieveFinalizationSecratryDtl(qtId);
			retrieveQuationTenderDetails(new Long(qtId), null);
			retrieveCommitteeAppointmentTabDtl();
		} else {

			qtId = ((SCQuotationTenderDataType) obj).getQtId().longValue();
			retrieveFinalizationSecratryDtl(qtId);
			retrieveQuationTenderDetails(new Long(qtId), null);
		}

		if (null == this.getManagedBean().getQt().getProcurementMode()) {
			this.getManagedBean().getQt().setProcurementMode(new PmParameter());
		}
		this.getManagedBean().setQuotationTenderDataType(obj);
		this.getManagedBean().setProcessName(res.getProcessName());

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

		// TODO Need to get it from front end and set it
		qtReqDTO.setOutcome(outcome);
		IQTDetailSO detailSO;
		try {
			ScQt qt = qtReqDTO.getQtdetail();
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
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
			} else if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
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
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
			} else {
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setPayLoad(res.getPojo());
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				qtReqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
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
														MessageConst.INFO_700005,
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
		
		//Added by Fara on 03092014 - Fix for Approval tab if user dint visit the Committee Appointment tab
		List<TabBean> tabList = this.getManagedBean().getScCommitteeMemberTabList();
		if(tabList == null || tabList.size() == 0){
			retrieveCommitteeAppointmentTabDtl();
		}
		
		updateQtDtls();
		debugLogger("retrieveApprovalTabDtl");
		// this.getManagedBean().setActiveIndex(3);
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
			MenuModel menuModel = new DefaultMenuModel();
			if (requiredApproval != null) {
				for (ScQtApproval qtApproval : requiredApproval) {
					 qtApproval = checkIsApprovalExists(qtApproval,
					 existingApprovalTabs);
					qtApproval.setApprovalType(ApprovalCategory
							.getDisplayName(qtApproval.getApprovalCategory()
									.getParameterCode()));
					qtApproval.setScQtApprovalRemarks(initializeRemarks(
							qtApproval, qtApproval.getScQtApprovalRemarks(),
							ScQtApprovalRemark.class));
					// qtApproval.setScQtApprovalAttachment(null);
					qtApproval.setScQt(this.getManagedBean().getQt());

					/*
					 * // BUDGET_APPROVAL if
					 * (qtApproval.getApprovalCategory().getParameterCode()
					 * .equals(ParameterConst.APPROVAL_CATEGORY_BUDGET)) {
					 * tabBeans.add(new TabBean(qtApproval.getApprovalType(),
					 * qtApproval, approverSettingDtls(
					 * RoleCode.BUDGET_APPROVER, qtApproval))); } else
					 */if (qtApproval.getApprovalCategory().getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_BPK)) {
						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, null));
					} else if (qtApproval.getApprovalCategory()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_PTJ)) {
						if (SourcingConstants.APPROVE_ACTION
								.equalsIgnoreCase(qtApproval
										.getApproverAction())) {
							DualListModel<ScQtApproverSettings> ptjApprovals = approverSettingDtls(
									RoleCode.PUBLICATION_APPROVER, qtApproval);
							tabBeans.add(new TabBean(qtApproval
									.getApprovalType(), qtApproval,
									ptjApprovals));
						}
					} else if (qtApproval
							.getApprovalCategory()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE)) {
						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, null));
					} else if (qtApproval
							.getApprovalCategory()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT)) {
						tabBeans.add(new TabBean(
								qtApproval.getApprovalType(),
								qtApproval,
								approverSettingDtls(
										RoleCode.COMMITTEE_APPROVER, qtApproval)));
					} else if (qtApproval.getApprovalCategory()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_OTHERS)) {
						List<ScQtApprovalRequest> approvalRequest = qtApproval
								.getScQtApprovalDtls();
						if (null != approvalRequest
								&& !approvalRequest.isEmpty()) {
							for (int i = 0; i < approvalRequest.size(); i++) {
								PmParameter parameter = approvalRequest.get(i)
										.getDecessionType();
								if (null == parameter) {
									approvalRequest.get(i).setDecessionType(
											new PmParameter());
									ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
									approvalRemark
											.setApprovalRequest(approvalRequest
													.get(i));
									approvalRequest.get(i).setAppRequestRemark(
											approvalRemark);
								}
							}
						}
						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, null));
					}

				}
			}
			int approvalTabIndex = 0;
			for (TabBean approvalTabBean : tabBeans) {
				MenuItem approvalItem = new MenuItem();
				approvalItem.setValue(approvalTabBean.getTitleName());
				approvalItem.setUpdate("@form");
				approvalItem.setActionExpression(FacesScopes
						.createMethodExpression(facesContext,
								"#{qTDetailBB.approvalTabChangeAction("
										+ approvalTabIndex + ")}", null,
								new Class[] { Integer.class }));
				menuModel.addMenuItem(approvalItem);
				approvalTabIndex++;
			}
			this.getManagedBean().setApprovalMenuModel(menuModel);
			
			//Added by Fara on 03092014 - Fix for Approval tab if no data for approval
			if( menuModel.getContents() != null && menuModel.getContents().size() > 0){
				this.getManagedBean().setSelectedApprovalTabBean(tabBeans.get(0));
				this.getManagedBean().setTabList(tabBeans);
			}
		} finally {
			detailSO = null;
		}
	}

	public void retrieveSuppQualificationTabDtl() {
		debugLogger("retrieveSuppQualificationTabDtl");
		IPanelSupplierSO panelSupplierSO;
		IQTDetailSO QtDetailsSO;
		// this.getManagedBean().setActiveIndex(2);
		try {
			/*
			 * if (null == this.getManagedBean().getQtCategoryList() && null ==
			 * getManagedBean().getSuppCriteriarestrictedList() &&
			 * getManagedBean().getZoneTrees().isEmpty() &&
			 * getManagedBean().getCtZoneSuppliers().isEmpty()) {
			 */
			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());
			QuationTenderResDTO tenderResDTO = QtDetailsSO
					.retriveQTSuppQulCriDetails(tenderReqDTO);
			List<ScQtCategory> categoryResults = tenderResDTO
					.getCategoryResultBean().getResultList();
			if (isNotNullOREmpty(categoryResults)) {
				if (QTConstants.OPEN_TYPE.equalsIgnoreCase(tenderResDTO
						.getQtDetail().getQtType())) {
					this.getManagedBean().setQtCategoryList(categoryResults);
				} else if (QTConstants.RESTRICTED_TYPE
						.equalsIgnoreCase(tenderResDTO.getQtDetail()
								.getQtType())) {
					this.getManagedBean().setSuppCriteriarestrictedList(
							categoryResults);
				}
			}
			ScQt scQt = tenderResDTO.getQtDetail();
			this.getManagedBean().setAllStateCode(tenderResDTO.getStateCode());
			this.getManagedBean().setQt(scQt);
			if (null == scQt.getScQtSupplierCriteria()) {
				this.getManagedBean().setSupplierCriteria(
						new ScQtSupplierCriteria());
			} else {
				this.getManagedBean().setSupplierCriteria(
						scQt.getScQtSupplierCriteria());
			}
			if (!scQt.isPanel()) {
				if (scQt.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND
						&& this.getManagedBean().getAllStateCode() == 3
						&& !scQt.getScQtSupplierCriteria().isAllState()) {
					scQt.getScQtSupplierCriteria().setAllState(true);
				}
				if ((!scQt.isZonal())
						|| (scQt.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND && !scQt
								.getScQtSupplierCriteria().isAllState())) {
					List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
					if (null != tenderResDTO.getLocationDetails()) {
						for (TplSpecLoc localLoc : tenderResDTO
								.getLocationDetails()) {
							ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
							locCriteria.add(criteria);
						}
						if (this.getManagedBean().getZoneTrees().size() == 0
								&& this.getManagedBean().getZoneTrees() != null) {
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.constuctTreeNode(locCriteria));
						}
					}
				}
			} else {
				if (scQt.isZonal() && scQt.isPanel()) {
					this.getManagedBean().setSupplierCriteria(
							tenderResDTO.getQtDetail()
									.getScQtSupplierCriteria());
					List<CtPanelItem> itemsList = new ArrayList<>();
					itemsList.add(scQt.getScQtSupplierCriteria()
							.getCtPanelItem());
					this.getManagedBean().setSelectpanelItem(itemsList);
					this.getManagedBean().setCtzones(true);
					List<CtPanelZone> ctPanelZone = new ArrayList<CtPanelZone>();
					if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
						for (TplSpecZoneDtl zoneDtl : tenderResDTO
								.getZoneDetails()) {
							ctPanelZone.add(zoneDtl.getPanelZone());
						}
					}
					if (null != getManagedBean().getCtZoneSuppliers()
							&& getManagedBean().getCtZoneSuppliers().isEmpty()) {
						getManagedBean().getCtZoneSuppliers().add(
								LocationSingleton.getInstance()
										.constuctPanelTreeNodes(ctPanelZone));
					}
				} else if (scQt.isPanel() && !scQt.isZonal()) {
					if (null != scQt.getScQtSupplierCriteria().getCtPanelItem()
							&& null != scQt.getScQtSupplierCriteria()
									.getCtPanelItem().getPanelItemId()) {
						List<CtPanelItem> itemsList = new ArrayList<>();
						itemsList.add(scQt.getScQtSupplierCriteria()
								.getCtPanelItem());
						this.getManagedBean().setSelectpanelItem(itemsList);
						this.getManagedBean().setCtzones(false);
						PanelSupplierReqDTO supplierreqDTO = new PanelSupplierReqDTO();
						supplierreqDTO.setPanelItemId(scQt
								.getScQtSupplierCriteria().getCtPanelItem()
								.getPanelItemId());
						PanelSupplierResDTO panelSupplierResDTO = panelSupplierSO
								.retrievePanelItem(supplierreqDTO);
						this.getManagedBean().setTotalSupplier(0);
						this.getManagedBean().setCtZoneSupplierList(
								new ArrayList<CtPanelSupplier>());
						if (null != panelSupplierResDTO.getCtPanelItem()
								&& null != panelSupplierResDTO.getCtPanelItem()
										.getCtPanelSuppliers()
								&& !panelSupplierResDTO.getCtPanelItem()
										.getCtPanelSuppliers().isEmpty()) {
							this.getManagedBean().setTotalSupplier(
									panelSupplierResDTO.getCtPanelItem()
											.getCtPanelSuppliers().size());
							this.getManagedBean().setCtZoneSupplierList(
									new ArrayList<CtPanelSupplier>());
							this.getManagedBean().setCtZoneSupplierList(
									panelSupplierResDTO.getCtPanelItem()
											.getCtPanelSuppliers());
						}
					}
				}
			}
		} finally {
			QtDetailsSO = null;
		}
	}

	// To get the scdule tab details from so..
	public void retrieveScheduleTabDtl() {
		debugLogger("retrieveScheduleTabDtl");
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			QuationTenderResDTO resDTO = detailSO
					.retriveQTScheduleDetails(reqDTO);
			ISearchResultBean<ScQtContactPerson> contactPersons = resDTO
					.getContactPersons();
			List<ScQtContactPerson> qtContactPerson = new ArrayList<ScQtContactPerson>();
			if (null == contactPersons.getResultList()
					|| contactPersons.getResultList().isEmpty()) {
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
			} else {
				qtContactPerson.addAll(contactPersons.getResultList());
			}
			this.getManagedBean().setScQtContactPersons(qtContactPerson);
			this.getManagedBean().setPmuserContactpersonList(
					resDTO.getDeskOfficers().getResultList());
			QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
			if (this.getManagedBean().getQt().isSampleReq()) {
				if (this.getManagedBean().getQt().isZonal()) {
					this.getManagedBean().setScQtSampleSubmissionList(
							resDTO.getSampleSubmissionResultBean());
				} else {
					this.getManagedBean().setScQtSampleSubmission(
							resDTO.getSampleSubmissionResultBean().get(0));
					backingBean.retrievePmStateList();
				}

			}
			Date clsDate = this.getManagedBean().getQt().getClosingDate();
			if (null != clsDate) {
				this.getManagedBean().setProposalValidityUntil(
						DateUtils.addDays(clsDate, this.getManagedBean()
								.getQt().getQtValidityPeriod()));
			}
			backingBean.getDeskOfficerAdminAssistant();
		} finally {
			detailSO = null;
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
			} finally {
				detailSO = null;
			}
		}
	}

	public void retrieveDocumentTabDtl() {
		debugLogger("retrieveDocumentTabDtl");
		if (getManagedBean().getScQtDocReportList() == null) {
			IQTDetailSO detailSO;
			QuationTenderResDTO resDTO = null;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());
				List<ScQtDocReport> scQtDocReports = new ArrayList<ScQtDocReport>();
				if (this.getManagedBean().getScQtDocReportList() == null) {
					resDTO = detailSO.retriveQTDocDetails(reqDTO);
					if (resDTO.getReports() != null
							&& !resDTO.getReports().isEmpty()) {
						this.getManagedBean().setScQtDocReportList(
								resDTO.getReports());
					} else if (resDTO.getQtDocuments() != null
							&& resDTO.getQtDocuments().size() > 0) {
						for (PmDocument pmDoc : resDTO.getQtDocuments()) {
							ScQtDocReport scQtDocReport = new ScQtDocReport();
							scQtDocReport
									.setScQt(this.getManagedBean().getQt());
							scQtDocReport.setDocCategory(pmDoc
									.getDocCategoryCode().getParameterCode());
							scQtDocReport.setPmDocument(pmDoc);
							scQtDocReport.setTitle(pmDoc.getDocName());
							scQtDocReport.setDefinationType(pmDoc
									.getDocDefinitionTypeCode());
							scQtDocReport.setBrandNew(false);
							scQtDocReports.add(scQtDocReport);
						}
						this.getManagedBean().setScQtDocReportList(
								scQtDocReports);
					}
				}
			} finally {
				detailSO = null;
			}
		}
	}

	public void retrieveActivitiesTabDtl() {
		debugLogger("retrieveActivitiesTabDtl");
		/*
		 * if (getManagedBean().getQt().getQtStatus().getStatusId() > 60002) {
		 * this.getManagedBean().setActiveIndex(8); }
		 */
	}

	public void retrievePublicationApprovalTabDtl() {
		debugLogger("retrievePublicationApprovalTabDtl");
		IQTDetailSO detailSO;
		DualListModel<ScQtApproverSettings> dualListModel = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setApprovalType(ParameterConst.APPROVAL_CATEGORY_PTJ);
			QuationTenderResDTO resDTO = detailSO
					.retriveQTApprovalDetails(reqDTO);
			ScQtApproval publicationApproval = resDTO.getQtApproval();
			if (publicationApproval == null) {
				publicationApproval = new ScQtApproval();
				publicationApproval.setScQtApprovalRemarks(initializeRemarks(
						publicationApproval,
						publicationApproval.getScQtApprovalRemarks(),
						ScQtApprovalRemark.class));
				publicationApproval.setScQtApprovalAttachment(null);
				publicationApproval.setScQt(this.getManagedBean().getQt());
				List<ScQtApprovalRequest> qtApprovalRequests = publicationApproval
						.getScQtApprovalDtls();
				publicationApproval.setScQtApprovalDtls(qtApprovalRequests);
			} else {
				publicationApproval.setScQtApprovalRemarks(initializeRemarks(
						publicationApproval,
						publicationApproval.getScQtApprovalRemarks(),
						ScQtApprovalRemark.class));

			}
			dualListModel = approverSettingDtls(RoleCode.PUBLICATION_APPROVER,
					publicationApproval);
			this.getManagedBean().setPublicationApprovals(dualListModel);
			this.getManagedBean().setPublicationApproval(publicationApproval);
			log.info("Remark Size : "
					+ this.getManagedBean().getPublicationApproval()
							.getScQtApprovalRemarks().size());
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
		for (TreeNode treeNode : treeNodes) {
			List<TreeNode> stateNodes = treeNode.getChildren();
			for (TreeNode treeNode2 : stateNodes) {
				List<TreeNode> district = treeNode2.getChildren();
				if (null != district && !district.isEmpty()) {
					for (TreeNode devision : district) {
						if (devision.getChildCount() > 0) {
							for (TreeNode districtNode : devision.getChildren()) {
								ScQtLocCriteria criteria = ((ScQtLocCriteria) districtNode
										.getData());
								criteria.setPmDistrict(((ScQtLocCriteria) districtNode
										.getData()).getPmDistrict());
								criteria.setPmDivision(((ScQtLocCriteria) districtNode
										.getParent().getData()).getPmDivision());
								criteria.setPmState(((ScQtLocCriteria) treeNode2
										.getData()).getPmState());
								criteria.setScQtSupplierCriteria(this
										.getManagedBean().getSupplierCriteria());
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
		return results;
	}

	private List<TplSpecZoneDtl> getZoneBusinessOwnerLocations(
			List<TreeNode> treeNodes, TplSpecZoneDtl zoneDtl) {
		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		List<TplSpecZoneDtl> zoneList = new ArrayList<TplSpecZoneDtl>();
		for (TreeNode treeNode : treeNodes) {
			List<TreeNode> zoneNodes = treeNode.getChildren();
			zoneDtl = (TplSpecZoneDtl) zoneNodes.get(0).getData();
			for (TreeNode treeNode1 : zoneNodes) {
				List<TreeNode> stateNodes = treeNode1.getChildren();
				for (TreeNode treeNode2 : stateNodes) {
					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {
							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {
									TplSpecZoneLoc criteria = (TplSpecZoneLoc) districtNode
											.getData();
									criteria.setPmDistrict(((TplSpecZoneLoc) districtNode
											.getData()).getPmDistrict());
									criteria.setPmDivision(((TplSpecZoneLoc) districtNode
											.getParent().getData())
											.getPmDivision());
									criteria.setPmState(((TplSpecZoneLoc) treeNode2
											.getData()).getPmState());
									criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
											.getData()));
									setUserDetails(criteria);
									results.add(criteria);
								}
							} else {
								TplSpecZoneLoc criteria = (TplSpecZoneLoc) devision
										.getData();
								criteria.setPmDistrict(((TplSpecZoneLoc) devision
										.getData()).getPmDistrict());
								criteria.setPmState(((TplSpecZoneLoc) treeNode2
										.getData()).getPmState());
								criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
										.getData()));
								setUserDetails(criteria);
								results.add(criteria);
							}
						}
					} else {
						TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode2
								.getData();
						criteria.setPmState(((TplSpecZoneLoc) treeNode2
								.getData()).getPmState());
						criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
								.getData()));
						setUserDetails(criteria);
						results.add(criteria);
					}
				}
			}
			zoneDtl.setTplSpecZoneLocs(results);
			setUserDetails(zoneDtl);
			zoneList.add(zoneDtl);
		}
		return zoneList;
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
					if (!tabBean.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers().isEmpty()) {
						qtCommittes.add(tabBean.getScQtCommittee());
					}
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
			if (getManagedBean().getQt() == null) {
				tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
				if (tenderResDTO.getQtDetail() != null) {
					getManagedBean().setQt(tenderResDTO.getQtDetail());
					getManagedBean().getCtContract().setContractNo(this.getManagedBean().getQt().getExistingContractNo());
					if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
						if (tenderResDTO.getQtDetail().isZonal()) {
							if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
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
					if (getManagedBean().getQt().getEstimatedAmount() != null) {
						if (getManagedBean().getQt().getEstimatedAmount()
								.intValue() >= SourcingConstants.ESTMT_TWANTY_THOUSAND
								&& getManagedBean().getQt()
										.getEstimatedAmount().intValue() <= SourcingConstants.ESTMT_FIFTY_THOUSAND) {
							if (getManagedBean().getQt().getQtValidityPeriod() == null) {
								getManagedBean().getQt().setQtValidityPeriod(
										SourcingConstants.SIXTY_DAYS);
							}
							if (getManagedBean().getQt().getPublishPeriod() == null) {
								getManagedBean().getQt().setPublishPeriod(
										SourcingConstants.PUBLISH_THREE_DAYS);
							}
						} else if (getManagedBean().getQt()
								.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIFTY_THOUSAND
								&& getManagedBean().getQt()
										.getEstimatedAmount().intValue() <= SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
							if (getManagedBean().getQt().getQtValidityPeriod() == null) {
								getManagedBean().getQt().setQtValidityPeriod(
										SourcingConstants.NINTY_DAYS);
							}
							if (getManagedBean().getQt().getPublishPeriod() == null) {
								getManagedBean().getQt().setPublishPeriod(
										SourcingConstants.PUBLISH_SEVEN_DAYS);
							}
						} else if (getManagedBean().getQt()
								.getEstimatedAmount().intValue() > SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
							if (getManagedBean().getQt().getQtValidityPeriod() == null) {
								getManagedBean().getQt().setQtValidityPeriod(
										SourcingConstants.NINTY_DAYS);
							}
							if (getManagedBean().getQt().getPublishPeriod() == null) {
								getManagedBean()
										.getQt()
										.setPublishPeriod(
												SourcingConstants.PUBLISH_TWENTYONE_DAYS);
							}
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
			if (null == qtCommitte) {
				qtCommitte = new ScQtCommittee();
				qtCommitte.setScQt(this.getManagedBean().getQt());
				setUserDetails(qtCommitte);
				qtCommitte.setCommitteeActivities(null);
				qtCommitte.setQtCommitteRemarks(null);
				qtCommitte.setScQtMeetings(null);
				ScCommittee committee = new ScCommittee();
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					committee.setApprovalReq(true);
				}
				setUserDetails(committee);
				PmStatus committeStatus = new PmStatus();
				committeStatus.setStatusId(QTStatusConst.CM_PENDING_SUBMISSION);
				committee.setCommitteStatus(committeStatus);
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
		String commType = (String) event.getComponent().getAttributes()
				.get("value");
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setFinalizationSecratary(true);
		CommitteResDTO resDTO = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			List<ScCommitteeMember> commMembers = resDTO
					.getCommMemberResultBean().getResultList();
			this.getManagedBean().getQt()
					.setFinalizationSecratery(commMembers.get(0).getPmUser());
		} finally {
			committeSO = null;
		}
	}

	public void committeTabChangeAction(Integer selectedZoneTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("Committe Tab change Action");
		}
		this.getManagedBean().setSelectedTabIndex(selectedZoneTabIndex);
		TabBean committeTabBean = this.getManagedBean().getScCommitteeMemberTabList().get(selectedZoneTabIndex);
		this.getManagedBean().setSelectedTabBean(committeTabBean);
	}

	public void approvalTabChangeAction(Integer selectedZoneTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("ApprovalTabChange Action");
		}
		TabBean approvalTabBean = this.getManagedBean().getTabList().get(selectedZoneTabIndex);
		this.getManagedBean().setSelectedApprovalTabIndex(selectedZoneTabIndex);
		this.getManagedBean().setSelectedApprovalTabBean(approvalTabBean);
	}

	public void validateQtDetails() {
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		int activeIndex = this.getManagedBean().getActiveIndex();
		QuationTenderReqDTO reqDTO = null;
		QuationTenderResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
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
			QuationTenderReqDTO dto = prepareQTInfo();
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setScQt(dto.getQtdetail());
			committeReqDTO.setUser(new FacesScopes().getUserSessionBean());
			committeSO.validateAppointCommitteDtls(committeReqDTO);
			// reqDTO = prepareQTInfo();
			resDTO = detailSO.saveQTDtl(dto);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			retrieveCommitteeAppointmentTabDtl();
			break;
		case 2:
			reqDTO = prepareQTInfo();
			detailSO.validateSupplierQualificationDtls(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			retrieveSuppQualificationTabDtl();
			break;
		case 3:
			reqDTO = prepareQTInfo();
			detailSO.validateApprovalDetails(reqDTO);
			resDTO = detailSO.saveQTDtl(reqDTO);
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
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
				retrieveScheduleTabDtl();
			}
			break;
		case 6:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				detailSO.validateDeductionDtls(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				retrieveDeductionPaymentTabDtl();
			}
			break;
		case 7:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				retrieveDocumentTabDtl();
				break;
			}
		case 8:
			break;
		case 9:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				reqDTO = prepareQTInfo();
				detailSO.validateApprovalDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				retrievePublicationApprovalTabDtl();
			}
			break;
		}
		this.getManagedBean().setActiveIndex(activeIndex);
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
	}

	public void qtDetailsTabChange(Integer activeIndex) {
		int priviousIndex = this.getManagedBean().getActiveIndex();
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
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
			detailSO.validateApprovalDetails(prepareQTInfo());
			// retrieveApprovalTabDtl();
			break;

		case 4:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				detailSO.validateBsvDetails(prepareQTInfo());
			}
			break;

		case 5:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				detailSO.validateScheduleDetails(prepareQTInfo());
			}
			break;

		case 6:
			if (getManagedBean().getQt().getQtStatus().getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				detailSO.validateDeductionDtls(prepareQTInfo());
			}
			break;

		case 7:
			break;
		case 8:
			break;
		case 9:
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
			if (getManagedBean().getQt().getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				retriveTechnicalSpecification();
			} else {
				retrieveBriefingSitevisitTabDtl();
			}
			break;
		case 5:
			if (getManagedBean().getQt().getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				retriveFinancialSpecification();
			} else {
				retrieveScheduleTabDtl();
			}
			break;
		case 6:
			if (getManagedBean().getQt().getQtStatus().getStatusId() <= QTStatusConst.QT_PENDING_QT_SUBMISSION) {
				retrieveActivitiesTabDtl();
			} else {
				retrieveDeductionPaymentTabDtl();
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
					approvalTab.getScQtApproval().setScQtApprovalDtls(
							qtApproval.getScQtApprovalDtls());
					return approvalTab.getScQtApproval();
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
}
