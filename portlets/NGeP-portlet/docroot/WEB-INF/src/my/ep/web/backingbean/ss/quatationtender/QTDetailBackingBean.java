package my.ep.web.backingbean.ss.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.persistence.entity.ss.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.ss.ApprovalCategory;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.approval.ApprovalBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.ss.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.type.ss.quationtender.BPMOutComeTypes;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

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

	private static final String MODULE_NAME = "ss";

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

		List<ScQtGosodoBudget> gosodos = this.getManagedBean().getQt()
				.getScQtGosodoBudgets();
		if (gosodos == null) {
			gosodos = new ArrayList<ScQtGosodoBudget>();

			this.getManagedBean().getQt().setScQtGosodoBudgets(gosodos);
		}

		ScQtGosodoBudget scQtGosodoBudget = new ScQtGosodoBudget();

		scQtGosodoBudget.setPmGo(new PmGo());
		scQtGosodoBudget.setPmSo(new PmSo());
		scQtGosodoBudget.setPmDo(new PmDo());
		scQtGosodoBudget.setScQt(this.getManagedBean().getQt());
		gosodos.add(scQtGosodoBudget);
	}

	/*
	 * Method to delete gosodo from table
	 */
	public void deleteGoSoDoItem(ValueChangeEvent changeEvent) {
		ScQtGosodoBudget gosodoBudget = (ScQtGosodoBudget) changeEvent
				.getNewValue();

		List<ScQtGosodoBudget> gosodos = this.getManagedBean().getGosodos();
		if (gosodos.contains(gosodoBudget)) {
			gosodos.remove(gosodoBudget);
		}
	}

	/*
	 * Onchange of gos to fetch the so's list
	 */
	public void handleGoChanged(ScQtGosodoBudget qtGosodoBudget) {
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

			IObjectSO objectSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			this.getManagedBean().setSoList(objResDTO.getPmSoList());
		} else {
			this.getManagedBean().setSoList(new ArrayList<PmSo>());
		}

		qtGosodoBudget.setPmSo(new PmSo());

		handleSoChanged(qtGosodoBudget);
	}

	/*
	 * OnChange of So's get the do's list
	 */
	public void handleSoChanged(ScQtGosodoBudget qtGosodoBudget) {
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

			IObjectSO objectSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveDOListBySOCode(reqDTO);

			this.getManagedBean().setDoList(objResDTO.getPmDoList());
		} else {
			this.getManagedBean().setDoList(new ArrayList<PmDo>());
		}

		qtGosodoBudget.setPmDo(new PmDo());
	}

	public void handleDoChange(ScQtGosodoBudget qtGosodoBudget) {
		PmDo pmDo = qtGosodoBudget.getPmDo();

		if (pmDo != null && pmDo.getDoId() != null) {
			List<PmDo> doList = this.getManagedBean().getDoList();
			for (PmDo pdo : doList) {
				if (pdo.getDoId().equals(pmDo.getDoId())) {
					qtGosodoBudget.setPmDo(pdo);
					break;
				}

			}

		}
	}

	/**
	 * Method to save the QT details
	 */
	public String saveQtDetailsAction() {
		debugLogger("saveQtDetailsAction");

		IQTDetailSO detailSO;

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTDetailSO.class);

			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();

			QuationTenderResDTO quationTenderResDTO = (QuationTenderResDTO) detailSO
					.saveQTDtl(quationTenderReqDTO);

			this.getManagedBean().setQt(quationTenderResDTO.getQtDetail());
			return "/xhtml/shared/acknowledge.xhtml";
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(),
							"Sorry we are Unable to Save the Details"));

		} finally {
			detailSO = null;
		}
		return "";
	}

	public String submitQTDetailsAction() {
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTDetailSO.class);

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

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));

				return "/xhtml/shared/acknowledge.xhtml";

			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
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

				IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject("pm",
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

				IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject("pm",
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

	private void populateUserMinistryList() {
		debugLogger("populateUserMinistryList");

		PmUser user = new FacesScopes().getUserSessionBean();

		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> userMinistryList = resDTO.getOrgProfileList();

			this.getManagedBean().setUserMinistryList(userMinistryList);
		}
	}

	public List<PmGo> getGoList() {
		debugLogger("getGoList");

		List<PmGo> goList = this.getManagedBean().getGoList();

		if (goList == null) {
			IObjectSO objectSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveGOList();

			goList = objResDTO.getPmGoList();

			this.getManagedBean().setGoList(goList);
		}

		return goList;
	}

	/**
	 * Add new Quotation/Tender action
	 * 
	 * @return MAINTANANCE_PAGE
	 */
	public String newAction() {
		ScQt scQt = new ScQt();

		scQt.setMinistryOrgProfile(new PmOrgProfile());
		scQt.setDepartmentOrgProfile(new PmOrgProfile());
		scQt.setPmOrgProfile(new PmOrgProfile());

		handlePTJName();
		populateUserMinistryList();
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, IQTDetailSO.class);
		QuationTenderResDTO dto = detailSO
				.retriveQTDetails(new QuationTenderReqDTO());
		this.getManagedBean().setGoList(dto.getGoList());

		this.getManagedBean().setQt(scQt);
		onChangeAction();
		return QTConstants.MAINTANANCE_PAGE + "?faces-redirect=true";
	}

	/**
	 * 
	 */
	public void handlePTJName() {
		debugLogger("handleDepartmentProfileChanged");

		PmUser user = new FacesScopes().getUserSessionBean();

		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			// reqDTO.setParentOrgProfileId(parentOrgProfileId);

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			this.getManagedBean().setUserDivisionList(
					resDTO.getOrgProfileList());
		}
	}

	/**
	 * Method to return the maintance page
	 * 
	 * @return maintanacepage
	 */
	public String editAction(ScQt scQt) {
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);

			ScQt selectedScQt = resDTO.getQtDetail();

			populateParentOrgProfile(selectedScQt);

			this.getManagedBean().setQt(selectedScQt);

			onChangeAction();
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), "Sorry We are unable to process."));

		} finally {
			detailSO = null;
		}
		return QTConstants.MAINTANANCE_PAGE;
	}

	/**
	 * Method to prepare scqt object with all dependent object.
	 * 
	 * @return QTDetailReq object
	 */
	public QuationTenderReqDTO prepareQTInfo() {

		ScQt scQt = this.getManagedBean().getQt();

		// TODO QT number need to be generated by code it should be unique

		if (scQt.isBrandNew()) {
			scQt.setQtNo(scQt.getFileNo());
		}
		// No need to set the below fields automatically set by listener
		PmUser user = new FacesScopes().getUserSessionBean();
		scQt.setCreatedBy(user.getUserId());
		scQt.setChangedBy(user.getUserId());
		scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		/*
		 * // Set Org Profile if (scQt.isBrandNew()) { Long profileId =
		 * scQt.getPmOrgProfile().getOrgProfileId();
		 * 
		 * if (profileId != null) { OrgProfileReqDTO orgUnitReqDTO = new
		 * OrgProfileReqDTO(); orgUnitReqDTO.setOrgProfileId(profileId);
		 * 
		 * OrgProfileResDTO unitResDTO = orgUnitSO
		 * .retrieveOrgProfileByProfileId(orgUnitReqDTO); PmOrgProfile
		 * orgProfile = unitResDTO.getPmOrgProfile();
		 * scQt.setPmOrgProfile(orgProfile); } }
		 */

		// Setting the pm status for qt details
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(1l);

		IStatusSO statusSO;
		PmStatus status = null;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject("pm",
					IStatusSO.class);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			status = pmStatus.getPmStatus();
			scQt.setPmStatus(status);
		} catch (Exception e) {

		} finally {
			statusSO = null;
		}

		// Setting the category codes to qtdetails
		// TODO need to get it from managed bean and set it.
		List<ScQtCategory> categorys = null;
		scQt.setScQtCategorys(categorys);
		// Setting the suppliers to qtdetails
		// TODO need to get it form managed bean and set it
		List<ScQtSupplier> suppliers = null;
		scQt.setScQtSuppliers(suppliers);
		// Setting the qtcriteria to qtdetails
		// TODO need to get it form managed bean and set it.

		scQt.setScQtCriterias(null);
		// Setting the approvalist to Qtdetails
		// TODO Need to get it from managed bean and set it.
		List<TabBean> tabBeans = getManagedBean().getTabList();
		List<ScQtApproval> approvals = new ArrayList<ScQtApproval>();
		if (tabBeans != null) {
			for (TabBean bean : tabBeans) {
				ScQtApproval tmpApproval = bean.getScQtApproval();
				tmpApproval.setScQtApprovalAttachment(null);
				tmpApproval.setScQt(scQt);
				tmpApproval.setPmStatus(status);
				approvals.add(tmpApproval);
			}
		}
		scQt.setScQtApprovals(approvals);
		// Setting the contact person to qtdetails.
		// TODO need to get it from managed bean and set it
		List<ScQtContactPerson> contactPersons = null;
		scQt.setScQtContactPersons(contactPersons);
		// Setting the qtBreifing details to Qtdetails
		// TODO need to get it from managed bean and set it
		List<ScQtBriefing> scQtBriefings = null;
		scQt.setScQtBriefings(scQtBriefings);
		// Setting the qtremarks to qtdetails.
		// TODO need to get it form managed bean and set it
		List<ScQtRemark> remarks = this.getManagedBean().getScQtRemarks();
		if (null != remarks) {
			ScQtRemark qtRemark = remarks.get(0);
			if (qtRemark.getRemark() != null) {
				qtRemark.setScQt(scQt);
				qtRemark.setPmStatus(status);
				scQt.setScQtRemarks(remarks);
			}
		}

		// Setting the qtdocuments to qtdetails
		// TODO need to get it from mangedbean and set it
		List<ScQtCheckList> documents = null;
		scQt.setScQtChecklists(documents);
		// Creating DTO object setting qtdetail object and making so call to
		// save.
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);

		return quationTenderReqDTO;

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
				bean.setSearchField(QTConstants.PROCUREMENT_MODE);
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
			// TODO Need to add status

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void onChangeAction() {

		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO dto = new QuationTenderReqDTO();

		dto.setQtdetail(getManagedBean().getQt());
		QuationTenderResDTO resDTO = detailSO.retriveQTApprovalDetails(dto);
		List<ScQtApproval> requiredApproval = resDTO.getApproval();
		List<TabBean> tabBeans = new ArrayList<TabBean>();
		if (requiredApproval != null) {

			for (ScQtApproval qtApproval : requiredApproval) {
				qtApproval.setApprovalType(ApprovalCategory
						.getDisplayName(qtApproval.getApprovalCategory()));
				tabBeans.add(new TabBean(qtApproval.getApprovalType(),
						qtApproval));
			}
			this.getManagedBean().setTabList(tabBeans);

		}
		if (this.getManagedBean().getVeryfiers() == null) {
			this.getManagedBean().setVeryfiers(resDTO.getVerifiers());
		}
		if (this.getManagedBean().getApprovers() == null) {
			this.getManagedBean().setApprovers(resDTO.getApprovers());
		}
		if (this.getManagedBean().getBbkApprovers() == null) {
			this.getManagedBean().setBbkApprovers(null);
		}

	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();

		if (QTConstants.QT_APPROVAL_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveApprovalTabDtl();
		} else if (QTConstants.QT_SUPPLIERQULIFICATION_TAB
				.equalsIgnoreCase(tabTitle)) {
			retrieveSuppQualificationTabDtl();
		} else if (QTConstants.QT_SCHDULE_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveScheduleTabDtl();
		} else if (QTConstants.QT_DEDUCTION_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveDeductionPaymentTabDtl();
		} else if (QTConstants.QT_DOCUMENTS_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveDocumentTabDtl();
		} else if (QTConstants.QT_REMARKS_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveRemarkTabDtl();
		}

	}

	/**
	 * 
	 */
	private void retrieveDeductionPaymentTabDtl() {
		if (getManagedBean().getScQtDeductionList() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						QTConstants.SS_MODULE, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);

				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();
				System.out.println("Deduction List : " + deductionList.size());

				getManagedBean().setScQtDeductionList(deductionList);

				// System.out.println("Deduction Type : " +
				// getManagedBean().getScQtDeductions().get(0).getDeductionType().getPmParameterType().getParameterName());
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));
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
		quotationTenderDataType.getQtId();

		log.info("requestList Id: ..........."
				+ quotationTenderDataType.getQtId());
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(quotationTenderDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		this.getManagedBean().setQt(tenderResDTO.getQtDetail());
		this.getManagedBean().setQuotationTenderDataType(
				quotationTenderDataType);
		populateParentOrgProfile(tenderResDTO.getQtDetail());
		onChangeAction();
		tenderResDTO = detailSO.retriveQTApprovalDetails(tenderReqDTO);
		ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();
		this.getManagedBean().setActiveIndex(2);
		List<TabBean> tabBeans = this.getManagedBean().getTabList();
		for (int i = 0; i < tabBeans.size(); i++) {
			TabBean tabBean = tabBeans.get(i);
			if (res.getProcessName().equalsIgnoreCase(
					tabBean.getScQtApproval().getApprovalType())) {
				this.getManagedBean().setSubTabIndex(i);
				break;
			}
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);
	}

	public String updateBpmTask() {
		QuationTenderReqDTO qtReqDTO = prepareQTInfo();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		qtReqDTO.setToken(ctxToken);
		qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
		qtReqDTO.setPayLoad(this.getManagedBean().getQuotationTenderDataType());
		// TODO Need to get it from front end and set it
		try {
			qtReqDTO.setOutcome(BPMOutComeTypes.getValue(this.getManagedBean()
					.getOutCome()));
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);

			QuationTenderResDTO resDTO = (QuationTenderResDTO) detailSO
					.updateBPMAction(qtReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""));
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry we are unable to Process.", ""));
			ex.printStackTrace();
		}
		return null;

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
		try {
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);

			QuationTenderResDTO resDTO = (QuationTenderResDTO) detailSO
					.updateBPMAction(qtReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process now", ""

					));
		}
		return null;

	}

	public void setOutComeAction(String outCome) {
		this.getManagedBean().setOutCome(outCome);

	}

	public void preRenderReviewView() {
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
		this.getManagedBean().setProcessName(res.getProcessName());
		SCQuotationTenderDataType quotationTenderDataType = (SCQuotationTenderDataType) obj;
		quotationTenderDataType.getQtId();

		log.info("requestList Id: ..........."
				+ quotationTenderDataType.getQtId());
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(quotationTenderDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		this.getManagedBean().setQt(tenderResDTO.getQtDetail());
		this.getManagedBean().setQuotationTenderDataType(
				quotationTenderDataType);
		populateParentOrgProfile(tenderResDTO.getQtDetail());
		onChangeAction();
		tenderResDTO = detailSO.retriveQTApprovalDetails(tenderReqDTO);
		ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);

	}

	private void retrieveApprovalTabDtl() {
		if (this.getManagedBean().getTabList() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						MODULE_NAME, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtdetail(getManagedBean().getQt());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTApprovalDetails(reqDTO);
				List<ScQtApproval> requiredApproval = resDTO.getApproval();
				List<TabBean> tabBeans = new ArrayList<TabBean>();

				if (requiredApproval != null) {

					for (ScQtApproval qtApproval : requiredApproval) {
						qtApproval.setApprovalType(ApprovalCategory
								.getDisplayName(qtApproval
										.getApprovalCategory()));
						qtApproval.setVerifyOption("O");
						qtApproval
								.setScQtApprovalAttachment(new ScQtApprovalAttachment());
						tabBeans.add(new TabBean(qtApproval.getApprovalType(),
								qtApproval));
					}
					this.getManagedBean().setTabList(tabBeans);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));

			} finally {
				detailSO = null;
			}
		}
	}

	private void retrieveSuppQualificationTabDtl() {
		if (getManagedBean().getQtCriteria() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						MODULE_NAME, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO
						.retriveQTSuppQulCriDetails(reqDTO);

				ScQtCriteria scQtCriteria = resDTO.getCriteria();
				if (scQtCriteria == null) {
					scQtCriteria = new ScQtCriteria();
				}
				getManagedBean().setQtCriteria(scQtCriteria);
				getManagedBean().setStates(resDTO.getStates());
				getManagedBean().setCategoryL1s((resDTO.getCategoryL1()));
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));

			} finally {
				detailSO = null;
			}
		}
	}

	private void retrieveScheduleTabDtl() {
		if (getManagedBean().getCountries() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						MODULE_NAME, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO
						.retriveQTScheduleDetails(reqDTO);

				getManagedBean().setCountries(resDTO.getCountries());
				getManagedBean().setBriefings(
						resDTO.getBriefingResultBean().getResultList());
				getManagedBean().setSiteBriefings(
						resDTO.getSiteVisitResultBean().getResultList());
				getManagedBean().setScQtContactPersons(
						resDTO.getContactPersonResultBean().getResultList());
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));

			} finally {
				detailSO = null;
			}
		}
	}

	private void retrieveDocumentTabDtl() {
		if (getManagedBean().getScQtChecklists() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						MODULE_NAME, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());
				// reqDTO.setQtId(328L);

				QuationTenderResDTO resDTO = detailSO
						.retriveQTDocDetails(reqDTO);

				getManagedBean().setScQtChecklists(
						resDTO.getDocumentResultBean().getResultList());
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));

			} finally {
				detailSO = null;
			}
		}
	}

	private void retrieveRemarkTabDtl() {
		if (getManagedBean().getScQtRemarks() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						MODULE_NAME, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO.retriveQtRemarks(reqDTO);
				List<ScQtRemark> remarkList = resDTO.getRemarkResultBean()
						.getResultList();

				getManagedBean().setScQtRemarks(
						resDTO.getRemarkResultBean().getResultList());
				initializeRemarkAndAttachement(getManagedBean()
						.getScQtRemarks());
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));

			} finally {
				detailSO = null;
			}
		} else {
			initializeRemarkAndAttachement(getManagedBean().getScQtRemarks());
		}
	}

	public void initializeRemarkAndAttachement(List<ScQtRemark> remarkList) {
		FacesScopes facesScopes = new FacesScopes();
		String screnName = null;
		if (remarkList != null && !remarkList.isEmpty()) {
			Long userId = remarkList.get(0).getCreatedBy();
			IUserSO userSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.PM_MODULE, IUserSO.class);
			UserReqDTO dto = new UserReqDTO();

			dto.setUserId(userId);
			UserResDTO userResDTO = userSO.retrieveUserDetails(dto);
			screnName = userResDTO.getPmUser().getLoginId();
		}
		if (remarkList.size() == 0 || !compareUserName(screnName)) {
			ScQtRemark remark = new ScQtRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
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
						MODULE_NAME, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

				populateQTSearchCriteria(qtnTenderSearchBean, searchBean);
				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO resDTO = detailSO.getQuationTenders(reqDTO);

				return resDTO.getQtResultBean();
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e
								.toString(),
								"Sorry we are Unable to Save the Details"));
			} finally {
				detailSO = null;
			}

			return null;
		}
	}

}
