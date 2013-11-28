package my.ep.web.backingbean.ss.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
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
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtContactPerson;
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
import com.sun.imageio.plugins.bmp.BMPConstants;

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
	private IQTDetailSO detailSO = null;
	private ICategorysSO categorysSO = null;
	private IStateSO stateSo = null;
	private IDistrictSO districtSO = null;
	private IOrgProfileSO orgUnitSO = null;
	private IStatusSO statusSO = null;
	

	public QTDetailBackingBean() {

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, IQTDetailSO.class);

		ICategorysSO categorysSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", ICategorysSO.class);

		IStateSO stateSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStateSO.class);
		IDistrictSO districtSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);

		IOrgProfileSO orgUnitSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IOrgProfileSO.class);

		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStatusSO.class);

		this.detailSO = detailSO;

		this.categorysSO = categorysSO;
		this.stateSo = stateSO;
		this.districtSO = districtSO;
		this.orgUnitSO = orgUnitSO;
		this.statusSO = statusSO;
		log.info("inside constructor");
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
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

		QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();

		try {
			QuationTenderResDTO quationTenderResDTO = detailSO
					.saveOrUpdateQT(quationTenderReqDTO);

			this.getManagedBean().setQt(quationTenderResDTO.getQtDetail());

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));

		}
		return "";
	}

	public String submitQTDetailsAction() {
		QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
		try {
			QuationTenderResDTO quationTenderResDTO = detailSO
					.submitQT(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Committe Type. : "

						));

				return "/xhtml/shared/acknowledge.xhtml";

			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to search the quationtenders based on serach date
	 */
	public void searchQTAction() {

		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getQtnTenderSearchBean();
			List<ScQt> searchQt = getSearchQTs(qtnTenderSearchBean);

			this.getManagedBean().setQtList(searchQt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method for clone search qts
	 */

	public void cloneSearchQtAction() {
		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getCloneQtnTenderSearchBean();
			List<ScQt> cloneSearchQt = getSearchQTs(qtnTenderSearchBean);

			this.getManagedBean().setCloneTenderList(cloneSearchQt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Clone Popup Action - To clear the clone search result for next time
	 */
	public void clonePopupAction() {
		this.getManagedBean().setCloneQtnTenderSearchBean(
				new QtnTenderSearchBean());

		System.out.println("inside clear cloneQT-------------------");
		List<ScQt> cloneQtList = this.getManagedBean().getCloneTenderList();
		if (!cloneQtList.isEmpty()) {
			this.getManagedBean().getCloneTenderList().clear();
		}

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
	 * Method to return the maintance page
	 * 
	 * @return maintanacepage
	 */
	public String editAction(ScQt scQt) {

		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtDetailId(scQt.getQtId());
		QuationTenderResDTO resDTO = detailSO.retriveQtById(reqDTO);

		ScQt selectedScQt = resDTO.getQtDetail();

		populateParentOrgProfile(selectedScQt);

		this.getManagedBean().setQt(selectedScQt);

		onChangeAction();
		return QTConstants.MAINTANANCE_PAGE;
	}

	private void populateParentOrgProfile(ScQt scQt) {
		PmOrgProfile parentOrgProfile = scQt.getPmOrgProfile()
				.getParentOrgProfile();

		while (parentOrgProfile != null) {
			// Ministry
			if (OrganizationType.MINISTRY.value().equals(
					parentOrgProfile.getPmCode().getCode())) {
				scQt.setMinistryOrgProfile(parentOrgProfile);
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getPmCode().getCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}

	/**
	 * Method to prepare scqt object with all dependent object.
	 * 
	 * @return QTDetailReq object
	 */
	private QuationTenderReqDTO prepareQTInfo() {

		ScQt scQt = this.getManagedBean().getQt();

		// TODO QT number need to be generated by code it should be unique
		scQt.setQtNo(scQt.getFileNo());

		// No need to set the below fields automatically set by listener
		scQt.setCreatedBy(1l);
		scQt.setChangedBy(1l);
		scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		// Set Org Profile
		if (scQt.isBrandNew()) {
			Long profileId = scQt.getPmOrgProfile().getOrgProfileId();

			if (profileId != null) {
				OrgProfileReqDTO orgUnitReqDTO = new OrgProfileReqDTO();
				orgUnitReqDTO.setOrgProfileId(profileId);

				OrgProfileResDTO unitResDTO = orgUnitSO
						.retrieveOrgProfileByProfileId(orgUnitReqDTO);
				PmOrgProfile orgProfile = unitResDTO.getPmOrgProfile();
				scQt.setPmOrgProfile(orgProfile);
			}
		}

		// Setting the pm status for qt details
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(1l);
		StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
		PmStatus status = pmStatus.getPmStatus();
		scQt.setPmStatus(status);

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

		for (TabBean bean : tabBeans) {
			ScQtApproval tmpApproval = bean.getScQtApproval();
			if(tmpApproval.getScQt() == null){
			tmpApproval.setScQt(scQt);
			}
			tmpApproval.setPmStatus(status);
			approvals.add(tmpApproval);
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
		List<ScQtRemark> remarks = null;
		scQt.setScQtRemarks(remarks);
		// Setting the qtdocuments to qtdetails
		// TODO need to get it from mangedbean and set it
		List<ScQtCheckList> documents = null;
		scQt.setScQtChecklists(documents);
		// Creating DTO object setting qtdetail object and making so call to
		// save.
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);

		FacesScopes facesScopes= new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		quationTenderReqDTO.setToken(ctxToken);
		return quationTenderReqDTO;

	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @return quatationTenderReq object
	 */
	private List<ScQt> getSearchQTs(QtnTenderSearchBean qtnTenderSearchBean) {

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

			reqDTO.setQtSearchBean(qtSearchBean);
		}
		QuationTenderResDTO dto = null;
		try {
			dto = detailSO.getQuationTenders(reqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));

		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();
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
		if(this.getManagedBean().getVeryfiers() == null){
			this.getManagedBean().setVeryfiers(resDTO.getVerifiers());
		}if(this.getManagedBean().getApprovers() == null){
			this.getManagedBean().setApprovers(resDTO.getApprovers());
		}if(this.getManagedBean().getBbkApprovers() == null){
			this.getManagedBean().setBbkApprovers(null);
		}
		
	}

	public void onTabChangeAction(TabChangeEvent event) {
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		String tabTitle = event.getTab().getTitle();
		QuationTenderReqDTO dto = new QuationTenderReqDTO();
		dto.setQtdetail(getManagedBean().getQt());
		if (QTConstants.QT_APPROVAL_TAB.equalsIgnoreCase(tabTitle)) {
			this.getManagedBean().setTabList(null);

			QuationTenderResDTO resDTO = detailSO.retriveQTApprovalDetails(dto);
			if (this.getManagedBean().getTabList() == null) {
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
			}

		} else if (QTConstants.QT_SUPPLIERQULIFICATION_TAB
				.equalsIgnoreCase(tabTitle)) {

			QuationTenderResDTO resDTO = detailSO
					.retriveQTSuppQulCriDetails(dto);
		} else if (QTConstants.QT_SCHDULE_TAB.equalsIgnoreCase(tabTitle)) {
			QuationTenderResDTO resDTO = detailSO.retriveQTScheduleDetails(dto);
		} else if (QTConstants.QT_DOCUMENTS_TAB.equalsIgnoreCase(tabTitle)) {
			QuationTenderResDTO resDTO = detailSO.retriveQTDocDetails(dto);
		} else if (QTConstants.QT_REMARKS_TAB.equalsIgnoreCase(tabTitle)) {
			QuationTenderResDTO resDTO = detailSO.retriveQtRemarks(dto);
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
		FacesScopes facesScopes= new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		//String ctx = bpmSO.authenticate("roslan", "welcome1");
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
		tenderReqDTO.setQtDetailId(new Long(quotationTenderDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		this.getManagedBean().setQt(tenderResDTO.getQtDetail());
		this.getManagedBean().setQuotationTenderDataType(quotationTenderDataType);
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
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.MAINTANANCE_PAGE + "?faces-redirect=true");
	}
	
	public String updateBpmTask(){
		QuationTenderReqDTO qtReqDTO =prepareQTInfo();

		qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
		
		qtReqDTO.setQuotationTenderDataType(this.getManagedBean().getQuotationTenderDataType());
		//TODO Need to get it from front end and set it
		try{
		qtReqDTO.setOutcome(BPMOutComeTypes.getValue(this.getManagedBean().getOutCome() ));
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);

		QuationTenderResDTO resDTO = detailSO.updateQTBPMTask(qtReqDTO);
		if (resDTO.isSuccessful()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record saved successfully.",
							"Committe Type. : "

					));

			return "/xhtml/shared/acknowledge.xhtml";

		} else {
			throw new EpException("Failed.");
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
		
	}
	public String updateBpmTask(String outcome){
		
		QuationTenderReqDTO qtReqDTO =prepareQTInfo();

		qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
		
		qtReqDTO.setQuotationTenderDataType(this.getManagedBean().getQuotationTenderDataType());
		//TODO Need to get it from front end and set it
		
		qtReqDTO.setOutcome(outcome);
		try{
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);

		QuationTenderResDTO resDTO = detailSO.updateQTBPMTask(qtReqDTO);
		if (resDTO.isSuccessful()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record saved successfully.",
							"Committe Type. : "

					));

			return "/xhtml/shared/acknowledge.xhtml";

		} else {
			throw new EpException("Failed.");
		}}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
		
	}

	public void setOutComeAction(String outCome){
		this.getManagedBean().setOutCome(outCome);
		
	}
	public void preRenderReviewView(){
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
		FacesScopes facesScopes= new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		//String ctx = bpmSO.authenticate("roslan", "welcome1");
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
		tenderReqDTO.setQtDetailId(new Long(quotationTenderDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		this.getManagedBean().setQt(tenderResDTO.getQtDetail());
		this.getManagedBean().setQuotationTenderDataType(quotationTenderDataType);
		populateParentOrgProfile(tenderResDTO.getQtDetail());
		onChangeAction();
		tenderResDTO = detailSO.retriveQTApprovalDetails(tenderReqDTO);
		ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();
	
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.MAINTANANCE_PAGE + "?faces-redirect=true");
	
	}
	

}
