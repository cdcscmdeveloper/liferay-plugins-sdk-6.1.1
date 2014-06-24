package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
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
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalCategory;
import my.ep.type.sc.CommitteType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
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
		handlePTJName();
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
	public void handleGoChanged(ScQtBudget qtGosodoBudget) {
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
		} else {
			this.getManagedBean().setSoList(new ArrayList<PmSo>());
		}

		qtGosodoBudget.setPmSo(new PmSo());

		// handleSoChanged(qtGosodoBudget);
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
	public String saveQtDetailsAction() {
		debugLogger("saveQtDetailsAction");

		IQTDetailSO detailSO;

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();

			QuationTenderResDTO quationTenderResDTO = (QuationTenderResDTO) detailSO
					.saveQTDtl(quationTenderReqDTO);
			this.getManagedBean().setQt(quationTenderResDTO.getQtDetail());

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
														MessageConst.INFO_700004,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
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

	public String submitQTDetailsAction() {
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

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

	/*
	 * private void populateUserMinistryList() {
	 * debugLogger("populateUserMinistryList");
	 * 
	 * PmUser user = new FacesScopes().getUserSessionBean();
	 * 
	 * if (user != null) { OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
	 * 
	 * reqDTO.setUserId(user.getUserId());
	 * reqDTO.setOrgType(OrganizationType.MINISTRY.value());
	 * 
	 * IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
	 * IOrgProfileSO.class); OrgProfileResDTO resDTO = orgProfileSO
	 * .retrieveOrgProfileListByOrgType(reqDTO);
	 * 
	 * List<PmOrgProfile> userMinistryList = resDTO.getOrgProfileList();
	 * 
	 * this.getManagedBean().setUserMinistryList(userMinistryList); } }
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
	 * 
	 */
	public void handlePTJName() {
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

			// auto populate the PTJ if only 1 PTJ found.
			if (pmOrgProfiles.size() == 1) {
				this.getManagedBean().getQt()
						.setPmOrgProfile(pmOrgProfiles.iterator().next());
			}

			this.getManagedBean().setUserDivisionList(pmOrgProfiles);
		}
	}

	public void handlePTJNameChanged(PmOrgProfile orgProfile) {
		List<PmOrgProfile> orgProfileList = getManagedBean()
				.getUserDivisionList();
		for (PmOrgProfile list : orgProfileList) {
			if (list.getOrgProfileId().equals(orgProfile.getOrgProfileId())) {
				getManagedBean().setOrgProfile(list);
			}
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
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			ScQt selectedScQt = resDTO.getQtDetail();
			this.getManagedBean().setQt(selectedScQt);
			if (QTStatusConst.ASSIGN_BO.equals(selectedScQt.getQtStatus()
					.getStatusId())) {
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
				//Code for selected consolidate Person should be highlight..
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
				if (getManagedBean().getSupplierCriteria() == null) {
					ScQtSupplierCriteria criteria = new ScQtSupplierCriteria();
					criteria.setQtZoneCriterias(new ScQtZoneCriteria());
					getManagedBean().setSupplierCriteria(
							new ScQtSupplierCriteria());
				}
				if (getManagedBean().getQt().getZoneLocationLevel() == null) {
					getManagedBean().getQt().setZoneLocationLevel(
							new PmParameter());
				}
				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (resDTO.getQtDetail().isZonal()) {
						if (isNotNullOREmpty(resDTO.getZoneDetails())) {
							for (TplSpecZoneDtl zone : resDTO.getZoneDetails()) {
								List<PmState> states = SourcingLocationHelper
										.getStateFromZoneLocationDTOs(zone
												.getTplSpecZoneLocs());

								List<PmDivision> divisions = SourcingLocationHelper
										.getDivisionFromZoneLocationDTOs(zone
												.getTplSpecZoneLocs());
								List<PmDistrict> districts = SourcingLocationHelper
										.getDistrictFromZoneLocationDTOs(zone
												.getTplSpecZoneLocs());

								List<PmCity> cities = SourcingLocationHelper
										.getCityFromZoneLocationDTOs(zone
												.getTplSpecZoneLocs());

								getManagedBean().getZoneGeneralTrees().add(
										LocationSingleton.getInstance()
												.buildGeneralZoneTree(zone,
														states, districts,
														divisions, cities));

							}
						}
					}
				}

				this.getManagedBean().setGoList(resDTO.getGoList());

				selectedScQt.setFileNo(scQt.getQtNo());
				this.getManagedBean().setQt(selectedScQt);
				if (getManagedBean().getQt().getEstimatedAmount() != null) {
					if (getManagedBean().getQt().getEstimatedAmount()
							.intValue() <= 500000) {
						getManagedBean().getQt().setPublishPeriod(7);
						getManagedBean().getQt().setQtValidityPeriod(60);
					} else if (getManagedBean().getQt().getEstimatedAmount()
							.intValue() > 500000) {
						getManagedBean().getQt().setPublishPeriod(21);
						getManagedBean().getQt().setQtValidityPeriod(90);
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

		ScQt scQt = this.getManagedBean().getQt();

		// QT number need to be generated by code it should be unique
		if (scQt.isBrandNew()) {
			scQt.setQtNo(getManagedBean().getQt().getQtNo());
		}
		if (!scQt.isSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}
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
			for (TabBean tabBean : committeTabs) {
				List<ScCommitteeMember> committeMembers = tabBean
						.getScQtCommittee().getScCommittee()
						.getScCommitteeMembers();
				if (!"BO".equals(tabBean.getScQtCommittee().getScCommittee()
						.getCommitteeType())) {
					if (null != committeMembers && !committeMembers.isEmpty()) {
						qtCommitteList = new ArrayList<ScQtCommittee>();

						qtCommitteList.add(tabBean.getScQtCommittee());
					}
				}
			}
			scQt.setScQtCommittees(qtCommitteList);
		}

		// Code for to update the supplier qualification criteria
		if (!scQt.isZonal()) {
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
		} else {
			List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
					this.getManagedBean().getZoneGeneralTrees(), null);
			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
				ScQtZoneCriteria zoneCriteria = this.getManagedBean()
						.getSupplierCriteria().getQtZoneCriterias();
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
					zoneLocal.add(tplSpecZoneDtl);
				}
				zoneCriteria.setTplSpecZoneDtls(zoneLocal);
				zoneCriteria.setScQtSupplierCriteria(this.getManagedBean()
						.getSupplierCriteria());
				setUserDetails(zoneCriteria);
				this.getManagedBean().getSupplierCriteria()
						.setQtZoneCriterias(zoneCriteria);
			}
			this.getManagedBean().getSupplierCriteria()
					.setScQtLocCriteria(null);
			this.getManagedBean().getSupplierCriteria()
					.setLocationLevel(scQt.getZoneLocationLevel());
		}

		this.getManagedBean().getSupplierCriteria()
				.setScQt(this.getManagedBean().getQt());
		setUserDetails(this.getManagedBean().getSupplierCriteria());
		scQt.setScQtSupplierCriteria(this.getManagedBean()
				.getSupplierCriteria());

		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();
		List<ScQtCategory> categoryList = new ArrayList<ScQtCategory>();
		if (openList != null && !openList.isEmpty()) {
			for (ScQtCategory scQtCategory : openList) {
				scQtCategory.setPmCategoryL1(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				scQtCategory.setPmCategoryL2(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2());
				scQtCategory.setScQt(this.getManagedBean().getQt());
				setUserDetails(scQtCategory);
				categoryList.add(scQtCategory);
			}
			scQt.setScQtCategorys(categoryList);
		}
		List<ScQtCategory> restrictedList = this.getManagedBean()
				.getSuppCriteriarestrictedList();
		if (restrictedList != null && !restrictedList.isEmpty()) {
			for (ScQtCategory scQtCategory : restrictedList) {
				scQtCategory.setPmCategoryL1(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				scQtCategory.setPmCategoryL2(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2());
				scQtCategory.setScQt(this.getManagedBean().getQt());
				setUserDetails(scQtCategory);
				categoryList.add(scQtCategory);
			}
			scQt.setScQtCategorys(categoryList);
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
		List<ScQtApproval> approvalList = new ArrayList<ScQtApproval>();
		if (tabList != null && !tabList.isEmpty()) {

			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(
							tabBean.getBudgetApprovalUsers().getTarget());
				}
				// tabBean.getScQtApproval().getApproverSettings();

				List<ScQtApprovalRemark> remarks = tabBean.getScQtApproval()
						.getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						if (remarks.get(i).getRemark() == null
								|| remarks.get(i).getRemark() == "") {
							tabBean.getScQtApproval().getScQtApprovalRemarks()
									.remove(i);
						}
					}
				}
				setUserDetails(tabBean.getScQtApproval());
				approvalList.add(tabBean.getScQtApproval());
			}
			// approvalList.add(getManagedBean().getScQtApproval());
			scQt.setScQtApprovals(approvalList);
		}

		// Setting the qtBreifing details to Qtdetails
		List<BriefingZoneBean> beansList = getManagedBean()
				.getBriefingZoneBean();
		if (beansList != null && !beansList.isEmpty()) {
			if (scQt.isZonal()) {
				for (BriefingZoneBean briefingZoneBean : beansList) {
					if (briefingZoneBean
							.getTplSpecZoneDtl()
							.getZoneName()
							.equalsIgnoreCase(
									this.getManagedBean().getBriefingZoneName())) {
						getManagedBean().getScQtBsv().setTplSpecZoneDtl(
								briefingZoneBean.getTplSpecZoneDtl());
					}

					List<ScQtBSVRemark> bsvRemarks = briefingZoneBean
							.getScQtBsv().getBsvRemarks();
					if (null != bsvRemarks) {
						for (int i = 0; i < bsvRemarks.size(); i++) {
							if (bsvRemarks.get(i).getRemark() == null
									|| bsvRemarks.get(i).getRemark() == "") {
								briefingZoneBean.getScQtBsv().getBsvRemarks()
										.remove(i);
							}
						}
					}
				}
				List<ScQtBsvDtl> briefBeansList = getManagedBean()
						.getBriefingZoneBean().get(0).getBriefingList();
				for (ScQtBsvDtl scQtBsvDtl : briefBeansList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllBriefingReq(
							getManagedBean().getScQtBsv().isAllBriefingReq());
				}
				getManagedBean().getScQtBsv().setScQtBsvDtls(briefBeansList);

				List<ScQtBsvDtl> siteBeansList = getManagedBean()
						.getBriefingZoneBean().get(0).getSitevisitList();
				for (ScQtBsvDtl scQtBsvDtl : siteBeansList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllBriefingReq(
							getManagedBean().getScQtBsv().isAllBriefingReq());
				}
				getManagedBean().getScQtBsv().getScQtBsvDtls()
						.addAll(siteBeansList);
			}

			getManagedBean().getScQtBsv().setScQt(getManagedBean().getQt());
			List<ScQtBsv> scQtBsvs = new ArrayList<ScQtBsv>();
			scQtBsvs.add(this.getManagedBean().getScQtBsv());
			scQt.setScQtBriefings(scQtBsvs);
		} else {
			List<ScQtBsvDtl> selectedBriefList = getManagedBean()
					.getBriefings();
			if (selectedBriefList != null) {
				for (ScQtBsvDtl scQtBsvDtl : selectedBriefList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllBriefingReq(
							getManagedBean().getScQtBsv().isAllBriefingReq());
				}
				getManagedBean().getScQtBsv().setScQtBsvDtls(selectedBriefList);
			}

			List<ScQtBsvDtl> selectedSiteVisitList = this.getManagedBean()
					.getSiteBriefings();
			if (selectedSiteVisitList != null) {
				for (ScQtBsvDtl scQtBsvDtl : selectedSiteVisitList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllSiteVisitReq(
							getManagedBean().getScQtBsv().isAllSiteVisitReq());
				}
				getManagedBean().getScQtBsv().getScQtBsvDtls()
						.addAll(selectedSiteVisitList);
			}

			if (getManagedBean().getScQtBsv() != null) {
				List<ScQtBSVRemark> bsvRemarks = getManagedBean().getScQtBsv()
						.getBsvRemarks();
				if (null != bsvRemarks) {
					for (int i = 0; i < bsvRemarks.size(); i++) {
						if (bsvRemarks.get(i).getRemark() == null
								|| bsvRemarks.get(i).getRemark() == "") {
							getManagedBean().getScQtBsv().getBsvRemarks()
									.remove(i);
						}
					}
				}
				getManagedBean().getScQtBsv().setScQt(getManagedBean().getQt());
				List<ScQtBsv> scQtBsvs = new ArrayList<ScQtBsv>();
				scQtBsvs.add(this.getManagedBean().getScQtBsv());
				scQt.setScQtBriefings(scQtBsvs);
			}
		}

		// Setting the contact person to qtdetails.
		List<ScQtContactPerson> list = new ArrayList<ScQtContactPerson>();

		List<ScQtContactPerson> contactPersonsList = this.getManagedBean()
				.getScQtContactPersons();
		List<ScQtContactPerson> deskOfficerAdminList = this.getManagedBean()
				.getPmuserContactpersonList();

		if (contactPersonsList != null && !contactPersonsList.isEmpty()) {
			list.addAll(contactPersonsList);
		}
		if (deskOfficerAdminList != null && !deskOfficerAdminList.isEmpty()) {
			list.addAll(deskOfficerAdminList);
		}

		if (list != null && !list.isEmpty()) {
			scQt.setScQtContactPersons(list);
		}

		if (scQt.isZonal()) {
			List<ScQtSampleSubmission> sampleSubmissionList = getManagedBean()
					.getSelectedSubmissionList();
			if (sampleSubmissionList != null && !sampleSubmissionList.isEmpty()) {
				for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
					scQtSampleSubmission.setScQt(getManagedBean().getQt());
				}
				scQt.setScQtSampleSubmission(sampleSubmissionList);
			}
		} else {
			List<ScQtSampleSubmission> sampleList = new ArrayList<ScQtSampleSubmission>();
			PmAddress address = getManagedBean().getPmAddress();
			// ScQtSampleSubmission submission =
			// getManagedBean().getScQtSampleSubmission().setAddress(address);

			for (ScQtSampleSubmission scQtSampleSubmission : sampleList) {
				scQtSampleSubmission.setAddress(address);
				scQtSampleSubmission.setScQt(getManagedBean().getQt());
				int index = sampleList.indexOf(scQtSampleSubmission);
				sampleList.set(index, scQtSampleSubmission);
			}
			scQt.setScQtSampleSubmission(sampleList);
		}

		// Setting the Deductions to qtdetails.
		List<ScQtDeduction> deductionList = getManagedBean()
				.getScQtDeductionList();
		if (deductionList != null && !deductionList.isEmpty()) {
			for (ScQtDeduction scQtDeduction : deductionList) {
				scQtDeduction.setScQtDeductionClauses(getManagedBean()
						.getScQtDeductionClauses());
			}
			scQt.setScQtDeductions(deductionList);
		}

		// Setting the Documents to qtdetails.
		List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> checkLists = getManagedBean().getScQtChecklists();
		if (checkLists != null && !checkLists.isEmpty()) {
			localChecklist.addAll(checkLists);
			scQt.setScQtChecklists(localChecklist);
		}

		// save.
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);

		return quationTenderReqDTO;
	}

	/**
	 * @param b
	 * @return
	 */
	/*
	 * private BaseEntity constructRemarks(boolean isZonal) { List<ScRemark>
	 * remarks = this.getManagedBean().getRemarks(); TplSpecZoneDtl specZoneDtl
	 * = null; ScQt qt = null;
	 * 
	 * if (isZonal) { specZoneDtl = this.getManagedBean().getSelectedZone();
	 * List<ScQtBreifingZoneRemark> zoneRemarks = new ArrayList<>(); for
	 * (ScRemark scRemark : remarks) { ScQtBreifingZoneRemark briefingRemark =
	 * (ScQtBreifingZoneRemark) scRemark; if (briefingRemark.getRemark() != null
	 * && !"".equals(briefingRemark.getRemark()))
	 * briefingRemark.setTplSpecZoneDtl(specZoneDtl);
	 * zoneRemarks.add(briefingRemark);
	 * 
	 * }
	 * 
	 * specZoneDtl.setQtBriefingRemarks(zoneRemarks); return specZoneDtl; } else
	 * { qt = this.getManagedBean().getQt(); List<ScQtBriefingRemark>
	 * normalRemarks = new ArrayList<>(); for (ScRemark scRemark : remarks) {
	 * ScQtBriefingRemark briefingRemark = (ScQtBriefingRemark) scRemark; if
	 * (briefingRemark.getRemark() != null && briefingRemark.getRemarkId() ==
	 * null) { briefingRemark.setScQt(qt); normalRemarks.add(briefingRemark);
	 * 
	 * } } qt.setScQtBriefingRemarks(normalRemarks); return qt; } }
	 */

	/**
	 * @return
	 */
	/*
	 * private ScQt constructApprovalRemarks() { List<ScRemark> remarks =
	 * this.getManagedBean().getRemarks(); ScQt qt =
	 * this.getManagedBean().getQt(); List<ScQtApprovalRemark> normalRemarks =
	 * new ArrayList<>(); for (ScRemark scRemark : remarks) { ScQtApprovalRemark
	 * approvalRemark = (ScQtApprovalRemark) scRemark;
	 * if(approvalRemark.getRemark() != null && approvalRemark.getRemarkId() ==
	 * null) { normalRemarks.add(approvalRemark); } }
	 * qt.setScQtBriefingRemarks(normalRemarks); return qt; }
	 */

	/**
	 * To get BUdget Approval Pick List in QT
	 */

	/*
	 * private List<ScQtApproval> createTargetApproval(ScQtApproval
	 * scQtApproval) { List<ScQtApproval> resultList = new
	 * ArrayList<ScQtApproval>(); scQtApproval = new ScQtApproval(); for (PmUser
	 * pmUser : getManagedBean().getBudgetApprovalUsers()) { scQtApproval.setP
	 * resultList.add(scQtApproval); } return
	 * getManagedBean().getBudgetApprovalUsers(); }
	 */

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
				bean.setSearchField("scQt.procurementMode.parameterCode");
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
			// Need to add status

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * 
	 */
	public void retriveFinancialSpecification() {
		debugLogger("retriveFinancialSpecification");
		this.getManagedBean().setActiveIndex(5);
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType("F");
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);

			if (resDto != null) {
				List<ScQtCheckList> financialSpecList = this.getManagedBean()
						.getFinancialSpecList();
				if (financialSpecList == null) {
					financialSpecList = new ArrayList<ScQtCheckList>();
				}
				List<ScQtCheckList> localList = new ArrayList<ScQtCheckList>();
				if (resDto.getSpecResultBean().getResultList().size() != financialSpecList
						.size()) {
					for (ScQtCheckList checklist : resDto.getSpecResultBean()
							.getResultList()) {
						/*
						 * checklist.setScQtSpec(this.getManagedBean()
						 * .getScQtSpec());
						 */
						localList.add(checklist);
					}
					this.getManagedBean().setFinancialSpecList(localList);
				}
			}
		} finally {
			specSO = null;
		}
	}

	/**
	 * 
	 */
	public void retriveTechnicalSpecification() {
		debugLogger("retriveTechnicalSpecification");
		this.getManagedBean().setActiveIndex(4);
		IQTSpecSO specSO;
		SpecificationResDTO resDto;

		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType("T");
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto != null) {
				List<ScQtCheckList> techSpecList = this.getManagedBean()
						.getTechnicalSpecList();
				if (techSpecList == null) {
					techSpecList = new ArrayList<ScQtCheckList>();
				}
				List<ScQtCheckList> localList = new ArrayList<ScQtCheckList>();
				if (resDto.getSpecResultBean().getResultList().size() != techSpecList
						.size()) {
					for (ScQtCheckList checklist : resDto.getSpecResultBean()
							.getResultList()) {
						/*
						 * checklist.setScQtSpec(this.getManagedBean()
						 * .getScQtSpec());
						 */
						localList.add(checklist);
					}
					this.getManagedBean().setTechnicalSpecList(localList);
				}
			}
		} finally {
			specSO = null;
		}
	}

	/**
	 * 
	 */
	public void retrieveBriefingSitevisitTabDtl() {
		debugLogger("retrieveBriefingSitevisitTabDtl");
		this.getManagedBean().setActiveIndex(6);
		if (getManagedBean().getQtBriefing() == null) {
			getManagedBean().setQtBriefing(new ScQtBsvDtl());
			getManagedBean().getQtBriefing().setAddress(new PmAddress());
			getManagedBean().getQtBriefing().getAddress()
					.setPmCity(new PmCity());
			getManagedBean().getQtBriefing().getAddress()
					.setPmDistrict(new PmDistrict());
			getManagedBean().getQtBriefing().getAddress()
					.setPmState(new PmState());
		}

		if (getManagedBean().getQtSiteBriefing() == null) {
			getManagedBean().setQtSiteBriefing(new ScQtBsvDtl());
		}
		if (getManagedBean().getQtSiteBriefing().getAddress() == null) {
			getManagedBean().getQtSiteBriefing().setAddress(new PmAddress());
		}
		if (getManagedBean().getQtSiteBriefing().getAddress().getPmCity() == null) {
			getManagedBean().getQtSiteBriefing().getAddress()
					.setPmCity(new PmCity());
		}
		if (getManagedBean().getQtSiteBriefing().getAddress().getPmDistrict() == null) {
			getManagedBean().getQtSiteBriefing().getAddress()
					.setPmDistrict(new PmDistrict());
		}
		if (getManagedBean().getQtSiteBriefing().getAddress().getPmState() == null) {
			getManagedBean().getQtSiteBriefing().getAddress()
					.setPmState(new PmState());
		}

		if (getManagedBean().getCountries() == null) {
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
							// List<ScQtBSVRemark> bsvRemarks
							// =scQtBsv.getBsvRemarks();
							initializeRemarks(scQtBsv);
							// scQtBsv.setBsvRemarks(bsvRemarks);
							BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
									specZoneDtl,
									briefingResDTO.getBriefingList(),
									briefingResDTO.getSiteVisitList(), scQtBsv);
							zoneBeans.add(briefingZoneBean);
						}
					}
					this.getManagedBean().setBriefingZoneBean(zoneBeans);

				} else {
					this.getManagedBean()
							.setBriefings(resDTO.getBriefingList());
					this.getManagedBean().setSiteBriefings(
							resDTO.getSiteVisitList());
					ScQtBsv qtBsv = resDTO.getScQtBsv();
					initializeRemarks(qtBsv);
					this.getManagedBean().setScQtBsv(qtBsv);

				}

				/*
				 * // getting Briefing Same As Site Visit value ScQt scQt =
				 * resDTO.getScQt(); // getManagedBean().setQt(scQt);
				 * 
				 * if (resDTO.getScQtBsv() != null) {
				 * 
				 * if (scQt.isZonal()) {
				 * getManagedBean().setZones(resDTO.getZonesList());
				 * List<ScQtBsvDtl> briefings = null; List<ScQtBsvDtl> siteVisit
				 * = null;
				 * 
				 * for (TplSpecZoneDtl tplSpecZoneDtl : getManagedBean()
				 * .getZones()) { briefings =
				 * retrieveBriefingSiteVisitDtlsByZoneNQtId( scQt.getQtId(),
				 * tplSpecZoneDtl.getSpecZoneId(), "B"); if (null != briefings
				 * && !briefings.isEmpty()) { this.getManagedBean().setScQtBsv(
				 * briefings.get(0).getScQtBsv()); } siteVisit =
				 * retrieveBriefingSiteVisitDtlsByZoneNQtId( scQt.getQtId(),
				 * tplSpecZoneDtl.getSpecZoneId(), "S"); if (null != siteVisit
				 * && !siteVisit.isEmpty()) { this.getManagedBean().setScQtBsv(
				 * siteVisit.get(0).getScQtBsv()); } // } BriefingZoneBean bean
				 * = new BriefingZoneBean( tplSpecZoneDtl, briefings,
				 * siteVisit); getManagedBean().getBriefingZoneBean().add(bean);
				 * getManagedBean().setSelectedZone(tplSpecZoneDtl); } } else {
				 * // Briefing Details Listing List<ScQtBsvDtl>
				 * briefingDetailslist = resDTO.getBriefingList();
				 * getManagedBean().setBriefings(briefingDetailslist); if
				 * (resDTO.getScQtBsv() != null) {
				 * this.getManagedBean().setScQtBsv(resDTO.getScQtBsv()); } //
				 * Site Visit Listing List<ScQtBsvDtl> sitevisitlist =
				 * resDTO.getSiteVisitList();
				 * getManagedBean().setSiteBriefings(sitevisitlist);
				 * 
				 * if (resDTO.getScQtBsv() != null) {
				 * this.getManagedBean().setScQtBsv(resDTO.getScQtBsv()); }
				 * this.getManagedBean().setRemarks(
				 * resDTO.getBriefingRemarks()); }
				 * 
				 * initializeRemarks();
				 */

				ScQtBsvRegistration scQtBsvRegistration = new ScQtBsvRegistration();
				scQtBsvRegistration.setSupplier(new SmSupplier());
				// scQtBriefingRegistration.setSmMofAccount(new
				// SmMofAccount());
				getManagedBean().setScQtBsvRegistration(scQtBsvRegistration);
				if (getManagedBean().getScQtBsv() == null) {
					getManagedBean().setScQtBsv(new ScQtBsv());
				}
			} finally {
				qTBriefingSO = null;
			}
		}
	}

	public void processChecked(AjaxBehaviorEvent e) {
		// doing some stuff here
		Boolean id = (Boolean) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
	}

	public void initializeRemarks(ScQtBsv qtBsv) {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		List<ScQtBSVRemark> remarks = qtBsv.getBsvRemarks();
		if (null == remarks) {
			remarks = new ArrayList<ScQtBSVRemark>();
		}
		ScQtBSVRemark remark = new ScQtBSVRemark();

		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		remark.setScQtBsv(qtBsv);
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.QT_BRIEFING_SITE_VISIT);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());
			remarks.add(remark);
		}
		qtBsv.setBsvRemarks(remarks);
	}

	/**
	 * @param qtId
	 * @param specZoneId
	 * @param attendenceType
	 * @return
	 */
	/*
	 * private List<ScQtBsvDtl> retrieveBriefingSiteVisitDtlsByZoneNQtId( Long
	 * qtId, Long zoneId, String bsvType) { IQTBriefingSO qTBriefingSO;
	 * 
	 * qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class); BriefingReqDTO
	 * reqDTO = new BriefingReqDTO(); reqDTO.setQtId(qtId);
	 * reqDTO.setSpecZoneId(zoneId); reqDTO.setBriefingType(bsvType);
	 * BriefingResDTO briefingResDTO = qTBriefingSO
	 * .retrieveBriefingSiteVisitList(reqDTO);
	 * this.getManagedBean().setRemarks(briefingResDTO.getBriefingRemarks());
	 * return briefingResDTO.getBriefingResultBean().getResultList(); }
	 */

	/**
	 * 
	 */
	public void retrieveCommitteeAppointmentTabDtl() {
		debugLogger("retrieveCommitteeAppointmentTabDtl");
		this.getManagedBean().setActiveIndex(1);
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		Long qtId = getManagedBean().getQt().getQtId();
		if (null != qtId && null == committeTabs) {
			String evaluationType = this.getManagedBean().getQt()
					.getEvaluationType();
			committeTabs = new ArrayList<TabBean>();
			// BO and specification committe is common for both evaluations..
			committeTabs.add(new TabBean("Business Owner",
					getQtCommiteDtlsByQtIdNType("BO", qtId)));
			committeTabs.add(new TabBean("Specification Committee",
					getQtCommiteDtlsByQtIdNType(
							CommitteType.getValue("SpecificationCommittee"),
							qtId)));
			// evaluation type is one tier only oc is added..
			if ("1".equals(evaluationType)) {
				committeTabs.add(new TabBean("Evaluation Committee",
						getQtCommiteDtlsByQtIdNType("EC", qtId)));

			}
			// evaluation type is two tier below committe are added
			else {
				committeTabs.add(new TabBean("Opening Committee",
						getQtCommiteDtlsByQtIdNType("OC", qtId)));
				committeTabs.add(new TabBean("TEC",
						getQtCommiteDtlsByQtIdNType("TEC", qtId)));
				committeTabs.add(new TabBean("FEC",
						getQtCommiteDtlsByQtIdNType("FEC", qtId)));
			}
			// For both evaluations we need finalization comitte
			// committeTabs.add(new TabBean("Finalization Secretary", new
			// ScCommittee()));
			/*
			 * if (null != getManagedBean().getQt().getEstimatedAmount() &&
			 * getManagedBean().getQt().getEstimatedAmount().intValue() >
			 * 500000) {
			 * 
			 * SelectItem[] selectItems = new SelectItem[2]; selectItems[0] =
			 * new SelectItem("PBB", "PBB"); selectItems[1] = new
			 * SelectItem("PBA", "PBA");
			 * getManagedBean().setFinalizeComm(selectItems);
			 * 
			 * tabBeans.add(new TabBean("Finalization Secretary",
			 * getFinalizationCommitteeTypeDtl("PBA", null))); } else {
			 * SelectItem[] selectItems = new SelectItem[2]; selectItems[0] =
			 * new SelectItem("QC", "QC"); selectItems[1] = new
			 * SelectItem("QCB", "QCB");
			 * getManagedBean().setFinalizeComm(selectItems);
			 * 
			 * tabBeans.add(new TabBean("Finalization Secretary",
			 * getFinalizationCommitteeTypeDtl("QC", null))); }
			 */
			this.getManagedBean().setScCommitteeMemberTabList(committeTabs);
		}
	}

	/**
	 * @param string
	 * @param object
	 * @return
	 */
	/*
	 * private ScCommittee getFinalizationCommitteeTypeDtl(String type, Long id)
	 * { CommitteReqDTO reqDTO = new CommitteReqDTO(); ScCommittee committee =
	 * null; reqDTO.setCommitteeType(type);
	 * reqDTO.setFinalizationSecratary(true); CommitteResDTO resDTO = null;
	 * ICommitteSO committeSO; try { committeSO =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class); resDTO =
	 * committeSO .retrieveCommitteMembersByQtIdAndCommType(reqDTO);
	 * List<ScCommitteeMember> commMembers = resDTO
	 * .getCommMemberResultBean().getResultList(); if (!commMembers.isEmpty()) {
	 * committee = commMembers.get(0).getScCommittee();
	 * committee.setScCommitteeMembers(commMembers); } else { committee = new
	 * ScCommittee(); committee .setScCommitteeMembers(new
	 * ArrayList<ScCommitteeMember>()); }
	 * 
	 * } finally { committeSO = null; } return committee; }
	 */

	/**
	 * @return
	 */
	/*
	 * private ScCommittee getCommitteeTypeDtl(String type, Long id) {
	 * CommitteReqDTO reqDTO = new CommitteReqDTO(); ScCommittee committee =
	 * null; reqDTO.setCommitteeType(type); reqDTO.setQtId(id); CommitteResDTO
	 * resDTO = null; ICommitteSO committeSO;
	 * 
	 * try { committeSO = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class); resDTO =
	 * committeSO .retrieveCommitteMembersByQtIdAndCommType(reqDTO);
	 * this.getManagedBean().setScQtCommittee(resDTO.getScQtCommittee());
	 * List<ScCommitteeMember> commMembers = resDTO
	 * .getCommMemberResultBean().getResultList(); for (ScCommitteeMember
	 * committeeMember : commMembers) { log.info("Member Name : " +
	 * committeeMember.getMemberName()); log.info("Member Role : " +
	 * committeeMember.getMemberRole()); log.info("Commitee Type : " +
	 * committeeMember.getScCommittee().getCommitteeType()); } if
	 * (!commMembers.isEmpty()) { committee =
	 * commMembers.get(0).getScCommittee();
	 * committee.setScCommitteeMembers(commMembers);
	 * 
	 * } else { committee = new ScCommittee(); PmStatus committeStatus = new
	 * PmStatus(); committeStatus.setStatusId(60200l);
	 * committee.setCommitteStatus(committeStatus); committee
	 * .setScCommitteeMembers(new ArrayList<ScCommitteeMember>()); }
	 * getManagedBean().setScCommittee(committee); } finally { committeSO =
	 * null; } return committee; }
	 */

	/**
	 * 
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

	/**
	 * 
	 */
	public void retrieveDeductionPaymentTabDtl() {
		debugLogger("retrieveDeductionPaymentTabDtl");
		this.getManagedBean().setActiveIndex(8);
		if (getManagedBean().getScQtDeductionList() == null) {
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
				System.out.println("Deduction List : " + deductionList.size());
				for (ScQtDeduction scQtDeduction : deductionList) {
					log.info("Deduction Id : "
							+ scQtDeduction.getQtDeductionId());
					Long deductionId = scQtDeduction.getQtDeductionId();
					reqDTO.setDeductionId(deductionId);
					if (deductionId != null) {
						resDTO = detailSO.retrieveDeDuctionClauseDtls(reqDTO);

						List<ScQtDeductionClause> deductionClauseList = resDTO
								.getDeductionClauseResultBean().getResultList();
						for (ScQtDeductionClause scQtDeductionClause : deductionClauseList) {
							scQtDeductionClause.setClauseDescRendered(String
									.valueOf(deductionId));
							log.info("Clause Description : "
									+ scQtDeductionClause.getClauseDesc());
						}
						getManagedBean().setScQtDeductionClauses(
								deductionClauseList);
					}
				}
				getManagedBean().setScQtDeductionList(deductionList);

			} finally {
				detailSO = null;
			}
		}
	}

	public void preRenderView() {
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
		this.getManagedBean().setOutComes(res.getAction());
		SCQuotationTenderDataType quotationTenderDataType = (SCQuotationTenderDataType) obj;
		retrieveQuationTenderDetails(new Long(quotationTenderDataType.getQtId()));
		if (null == this.getManagedBean().getQt().getProcurementMode()) {
			this.getManagedBean().getQt().setProcurementMode(new PmParameter());
		}
		this.getManagedBean().setQuotationTenderDataType(
				quotationTenderDataType);
		this.getManagedBean().setProcessName(res.getProcessName());
		// populateParentOrgProfile(this.getManagedBean().getQt());
		// onChangeAction();
		// tenderResDTO = detailSO.retriveQTApprovalDetails(tenderReqDTO);
		// ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);
	}

	public String updateBpmTask(String outcome) {

		QuationTenderReqDTO qtReqDTO = prepareQTInfo();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		qtReqDTO.setToken(ctxToken);
		qtReqDTO.setTaskId(this.getManagedBean().getTaskId());

		qtReqDTO.setPayLoad(this.getManagedBean().getQuotationTenderDataType());
		// TODO Need to get it from front end and set it

		qtReqDTO.setOutcome(outcome);
		IQTDetailSO detailSO;

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderResDTO resDTO = (QuationTenderResDTO) detailSO
					.updateBPMAction(qtReqDTO);
			if (resDTO.isSuccessful()) {

				ScQt scQt = resDTO.getQtDetail();

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

	public void setOutComeAction(String outCome) {
		this.getManagedBean().setOutCome(outCome);

	}

	public void retrieveApprovalTabDtl() {
		updateQtDtls();
		debugLogger("retrieveApprovalTabDtl");
		this.getManagedBean().setActiveIndex(3);
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(getManagedBean().getQt());
			QuationTenderResDTO resDTO = detailSO
					.retriveQTApprovalDetails(reqDTO);
			List<ScQtApproval> requiredApproval = resDTO.getApproval();
			List<TabBean> tabBeans = new ArrayList<TabBean>();
			if (requiredApproval != null) {
				for (ScQtApproval qtApproval : requiredApproval) {

					qtApproval.setApprovalType(ApprovalCategory
							.getDisplayName(qtApproval.getApprovalCategory()
									.getParameterId()));

					List<ScQtApprovalRemark> qtRemarkList = qtApproval
							.getScQtApprovalRemarks();
					if (null == qtRemarkList) {
						qtRemarkList = new ArrayList<ScQtApprovalRemark>();
					}
					ScQtApprovalRemark remark = initializeApprovalRemarks();
					remark.setScQtApproval(qtApproval);
					qtRemarkList.add(remark);
					qtApproval.setScQtApprovalRemarks(qtRemarkList);
					qtApproval.setScQtApprovalAttachment(null);
					qtApproval.setScQt(getManagedBean().getQt());

					List<ScQtApprovalRequest> qtApprovalRequests = qtApproval
							.getScQtApprovalDtls();

					qtApproval.setScQtApprovalDtls(qtApprovalRequests);

					// BUDGET_APPROVAL
					if (qtApproval.getApprovalCategory().getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_BUDGET)) {

						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, approverSettingDtls(
										RoleCode.BUDGET_APPROVER, qtApproval)));

					} else if (qtApproval.getApprovalCategory()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_BPK)) {
						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, null));
					} else if (qtApproval.getApprovalCategory()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_CATEGORY_PTJ)) {
						DualListModel<ScQtApproverSettings> ptjApprovals = approverSettingDtls(
								RoleCode.APPROVER, qtApproval);

						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, ptjApprovals));
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
						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval, approverSettingDtls(
										RoleCode.BUDGET_APPROVER, qtApproval)));
					}

				}
			}
			this.getManagedBean().setTabList(tabBeans);
		} finally {
			detailSO = null;
		}
	}

	/**
	 * @param scQtApprovalRemarks
	 */
	private ScQtApprovalRemark initializeApprovalRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		List<ScQtApprovalRemark> scQtApprovalRemarks = null;
		if (scQtApprovalRemarks == null) {
			scQtApprovalRemarks = new ArrayList<ScQtApprovalRemark>();
		}
		ScQtApprovalRemark approvalRemark = null;
		if (null == approvalRemark) {
			approvalRemark = new ScQtApprovalRemark();
		}
		approvalRemark.setCreatedBy(facesScopes.getUserSessionBean()
				.getUserId());
		approvalRemark
				.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		approvalRemark.setChangedBy(approvalRemark.getCreatedBy());
		approvalRemark.setCreatedUser(facesScopes.getUserSessionBean());
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.QT_BRIEFING_SITE_VISIT);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			approvalRemark.setPmStatus(statusResDTO.getPmStatus());
			// getManagedBean().getRemarks().add(approvalRemark);
		}
		scQtApprovalRemarks.add(approvalRemark);
		return approvalRemark;
	}

	public void retrieveSuppQualificationTabDtl() {
		debugLogger("retrieveSuppQualificationTabDtl");
		this.getManagedBean().setActiveIndex(2);
		IQTDetailSO QtDetailsSO;

		try {

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
				if ("O".equalsIgnoreCase(tenderResDTO.getQtDetail().getQtType())) {
					this.getManagedBean().setQtCategoryList(categoryResults);

				} else if ("R".equalsIgnoreCase(tenderResDTO.getQtDetail()
						.getQtType())) {
					this.getManagedBean().setSuppCriteriarestrictedList(
							categoryResults);
				}

			}

			if (!tenderResDTO.getQtDetail().isZonal()) {
				this.getManagedBean().setSupplierCriteria(
						tenderResDTO.getQtDetail().getScQtSupplierCriteria());

				if (this.getManagedBean().getZoneTrees() != null
						&& this.getManagedBean().getZoneTrees().size() == 0) {
					List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
					if (null != tenderResDTO.getLocationDetails()) {
						for (TplSpecLoc localLoc : tenderResDTO
								.getLocationDetails()) {
							ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
							locCriteria.add(criteria);
						}
						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.constuctTreeNode(locCriteria));
					}
				}
			}
		} finally {
			QtDetailsSO = null;
		}
	}

	public void retrieveScheduleTabDtl() {
		debugLogger("retrieveScheduleTabDtl");
		this.getManagedBean().setActiveIndex(7);
		if (getManagedBean().getScQtSampleSubmission() == null) {
			getManagedBean()
					.setScQtSampleSubmission(new ScQtSampleSubmission());
		}
		if (getManagedBean().getPmAddress() == null) {
			getManagedBean().setPmAddress(new PmAddress());
		}
		if (getManagedBean().getPmAddress().getPmCity() == null) {
			getManagedBean().getPmAddress().setPmCity(new PmCity());
		}
		if (getManagedBean().getPmAddress().getPmDistrict() == null) {
			getManagedBean().getPmAddress().setPmDistrict(new PmDistrict());
		}
		if (getManagedBean().getPmAddress().getPmState() == null) {
			getManagedBean().getPmAddress().setPmState(new PmState());
		}

		if (getManagedBean().getQt() != null
				&& null != getManagedBean().getQt().getClosingDate()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(getManagedBean().getQt().getClosingDate());
			cal.add(Calendar.DATE, getManagedBean().getQt()
					.getQtValidityPeriod());
			getManagedBean().setProposalValidityUntil(cal.getTime());

			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO
						.retriveQTScheduleDetails(reqDTO);

				// Contact Person
				List<ScQtContactPerson> contactPersonList = resDTO
						.getContactPersons().getResultList();
				System.out.println("Contact Person list: "
						+ contactPersonList.size());
				getManagedBean().setScQtContactPersons(contactPersonList);

				// Desk Officers
				List<ScQtContactPerson> deskOfficer = resDTO.getDeskOfficers()
						.getResultList();
				getManagedBean().setPmuserContactpersonList(deskOfficer);

				if (getManagedBean().getQt().isZonal()) {

					// Sample Submission
					List<ScQtSampleSubmission> submissionList = resDTO
							.getSampleSubmissionResultBean();
					if (submissionList != null) {

						List<TplSpecZoneDtl> dtl = getManagedBean().getZones();
						if (dtl != null) {
							for (TplSpecZoneDtl tplSpecZoneDtl : dtl) {
								ScQtSampleSubmission qtSampleSubmission = new ScQtSampleSubmission();
								qtSampleSubmission
										.setTplSpecZoneDtl(tplSpecZoneDtl);
								submissionList.add(qtSampleSubmission);
							}
						}
					}
					getManagedBean()
							.setScQtSampleSubmissionList(submissionList);
					log.info("Submission List : "
							+ getManagedBean().getScQtSampleSubmissionList());
				}

			} finally {
				detailSO = null;
			}
		}
	}

	public void retrieveDocumentTabDtl() {
		debugLogger("retrieveDocumentTabDtl");
		this.getManagedBean().setActiveIndex(9);
		if (getManagedBean().getScQtChecklists() == null) {
			IQTSpecSO iqtSpecSO;
			try {
				iqtSpecSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

				SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
				// specReqDTO.setQtId(scQt.getQtId());
				// specReqDTO.setSpecType(null);
				specReqDTO
						.setCheckListTypeCode(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
				specReqDTO.setProcurementCatCode(this.getManagedBean().getQt()
						.getPmProcurementCategory().getParameterCode());
				SpecificationResDTO specResDTO = iqtSpecSO
						.retrieveCheckLists(specReqDTO);

				if (specResDTO.getSpecResultBean() == null) {
					List<ScQtCheckList> checkListList = new ArrayList<ScQtCheckList>();
					for (PmDocument pmDoc : specResDTO.getDocumentResultBean()) {
						ScQtCheckList checkList = new ScQtCheckList();
						checkList.setTitle(pmDoc.getDocName());
						checkList.setDefinationType(pmDoc
								.getDocDefinitionTypeCode());
						checkList.setManditory(true);
						checkList.setAction(pmDoc.getChecklistActionCode());
						checkList.setBrandNew(false);
						checkListList.add(checkList);
					}
					this.getManagedBean().setScQtChecklists(checkListList);
				} else {
					getManagedBean().setScQtChecklists(
							specResDTO.getSpecResultBean().getResultList());
				}
			} finally {
				iqtSpecSO = null;
			}
		}
	}

	public void retrieveActivitiesTabDtl() {
		debugLogger("retrieveActivitiesTabDtl");
		this.getManagedBean().setActiveIndex(10);
	}

	/*
	 * private void retrieveRemarkTabDtl() { if
	 * (getManagedBean().getScQtRemarks() == null) { IQTDetailSO detailSO; try {
	 * detailSO = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	 * 
	 * QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
	 * reqDTO.setQtId(getManagedBean().getQt().getQtId());
	 * 
	 * QuationTenderResDTO resDTO = detailSO.retriveQtRemarks(reqDTO);
	 * 
	 * List<ScQtRemark> remarkList = resDTO.getRemarkResultBean()
	 * .getResultList(); System.out.println("Remark List : " +
	 * remarkList.size()); getManagedBean().setScQtRemarks(remarkList);
	 * 
	 * initializeRemarkAndAttachement(getManagedBean() .getScQtRemarks()); }
	 * catch (Exception ex) { ex.printStackTrace();
	 * FacesContext.getCurrentInstance() .addMessage( null, new
	 * FacesMessage(FacesMessage.SEVERITY_ERROR, ex.toString(),
	 * "Sorry We are unable to process."));
	 * 
	 * } finally { detailSO = null; } } else {
	 * initializeRemarkAndAttachement(getManagedBean().getScQtRemarks()); } }
	 */

	public void initializeRemarkAndAttachement(List<ScQtRemark> remarkList) {
		FacesScopes facesScopes = new FacesScopes();
		String screnName = null;
		if (remarkList != null && !remarkList.isEmpty()) {
			Long userId = remarkList.get(0).getCreatedBy();
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO dto = new UserReqDTO();

			dto.setUserId(userId);
			UserResDTO userResDTO = userSO.retrieveUserDetails(dto);
			screnName = userResDTO.getPmUser().getLoginId();
		}
		if (remarkList.size() == 0 || !compareUserName(screnName)) {
			ScQtRemark remark = new ScQtRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			// remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(facesScopes.getUserSessionBean()
					.getLoginId());
			getManagedBean().getScQtRemarks().add(0, remark);
			this.getManagedBean().setScQtRemarks(remarkList);

		} else {
			getManagedBean().setScQtRemarks(remarkList);
		}

	}

	/*
	 * private void populateCloneAll() { List<SelectItem> cloneAllList = new
	 * ArrayList<SelectItem>(); cloneAllList.add(new SelectItem("",
	 * "Select One")); CloneAll[] cloneAll = CloneAll.values(); for (CloneAll
	 * clone : cloneAll) { cloneAllList.add(new
	 * SelectItem(clone.getValue(),clone.getDisplayName())); }
	 * this.getManagedBean().setCloneAllList(cloneAllList);
	 * 
	 * }
	 */

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
			// zoneDtl=(TplSpecZoneDtl)treeNode.getData();
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
									TplSpecZoneLoc criteria = new TplSpecZoneLoc();

									criteria.setPmDistrict(((TplSpecZoneLoc) districtNode
											.getData()).getPmDistrict());
									criteria.setPmDivision(((TplSpecZoneLoc) districtNode
											.getParent().getData())
											.getPmDivision());
									criteria.setPmState(((TplSpecZoneLoc) treeNode2
											.getData()).getPmState());
									criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
											.getData()));
									// criteria.setScQtSupplierCriteria(supplierCriteria);
									criteria.setChangedBy(1L);
									criteria.setCreatedBy(1L);
									criteria.setCreatedDate(new Timestamp(
											System.currentTimeMillis()));
									results.add(criteria);

								}
							} else {

								TplSpecZoneLoc criteria = new TplSpecZoneLoc();
								criteria.setPmDistrict(((TplSpecZoneLoc) devision
										.getData()).getPmDistrict());
								criteria.setPmState(((TplSpecZoneLoc) treeNode2
										.getData()).getPmState());
								criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
										.getData()));
								// criteria.setScQtSupplierCriteria(supplierCriteria);
								criteria.setChangedBy(1L);
								criteria.setCreatedBy(1L);
								criteria.setCreatedDate(new Timestamp(System
										.currentTimeMillis()));
								results.add(criteria);
								/*
								 * zoneDtl.setTplSpecZoneLocs(results);
								 * zoneDtl.setTplSpecZone
								 * (getManagedBean().getScQt().
								 * getScQtZoneCriteria());
								 * zoneDtl.setChangedBy(1L);
								 * zoneDtl.setCreatedBy(1L);
								 * zoneDtl.setCreatedDate(new
								 * Timestamp(System.currentTimeMillis()));
								 * zoneList.add(zoneDtl);
								 */

							}

							// zoneDtl.setTplSpecZoneLocs(results);

						}
					} else {
						TplSpecZoneLoc criteria = new TplSpecZoneLoc();
						criteria.setPmState(((TplSpecZoneLoc) treeNode2
								.getData()).getPmState());
						criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
								.getData()));
						// criteria.setScQtSupplierCriteria(supplierCriteria);
						criteria.setChangedBy(1L);
						criteria.setCreatedBy(1L);
						criteria.setCreatedDate(new Timestamp(System
								.currentTimeMillis()));
						results.add(criteria);
					}
				}

			}

			zoneDtl.setTplSpecZoneLocs(results);
			// zoneDtl.setTplSpecZone(new ScQtZoneCriteria());
			zoneDtl.setChangedBy(1L);
			zoneDtl.setCreatedBy(1L);
			zoneDtl.setCreatedDate(new Timestamp(System.currentTimeMillis()));
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
				if (!tabBean.getScQtCommittee().getScCommittee()
						.getScCommitteeMembers().isEmpty()) {
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

	public void retrieveQuationTenderDetails(Long qtId) {
		IQTDetailSO QtDetailsSO;

		try {

			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(qtId);
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
			if (tenderResDTO.getQtDetail() != null) {
				if (tenderResDTO.getQtDetail() != null) {
					getManagedBean().setQt(tenderResDTO.getQtDetail());
					if (null == tenderResDTO.getQtSupplierCriteria()) {
						ScQtSupplierCriteria qtSupplierCriteria = new ScQtSupplierCriteria();
						qtSupplierCriteria
								.setQtZoneCriterias(new ScQtZoneCriteria());
						this.getManagedBean().setSupplierCriteria(
								qtSupplierCriteria);
					} else {
						this.getManagedBean().setSupplierCriteria(
								tenderResDTO.getQtSupplierCriteria());
					}
					if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
						if (tenderResDTO.getQtDetail().isZonal()) {
							if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
								for (TplSpecZoneDtl zone : tenderResDTO
										.getZoneDetails()) {
									List<PmState> states = SourcingLocationHelper
											.getStateFromZoneLocationDTOs(zone
													.getTplSpecZoneLocs());

									List<PmDivision> divisions = SourcingLocationHelper
											.getDivisionFromZoneLocationDTOs(zone
													.getTplSpecZoneLocs());
									List<PmDistrict> districts = SourcingLocationHelper
											.getDistrictFromZoneLocationDTOs(zone
													.getTplSpecZoneLocs());

									List<PmCity> cities = SourcingLocationHelper
											.getCityFromZoneLocationDTOs(zone
													.getTplSpecZoneLocs());

									getManagedBean().getZoneGeneralTrees().add(
											LocationSingleton.getInstance()
													.buildGeneralZoneTree(zone,
															states, districts,
															divisions, cities));

								}
							}

						}
					}
				}
			}
		}

		finally {
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

			if ("BO".equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if ("SC".equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if ("OC".equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if ("EC".equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if ("TEC".equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
			} else if ("FEC".equalsIgnoreCase(commType)) {
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

}
