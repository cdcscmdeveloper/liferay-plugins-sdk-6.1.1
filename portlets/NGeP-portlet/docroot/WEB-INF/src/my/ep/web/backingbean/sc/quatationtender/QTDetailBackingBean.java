package my.ep.web.backingbean.sc.quatationtender;

import java.math.BigDecimal;
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
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtBreifingZoneRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.sc.ApprovalCategory;
import my.ep.type.sc.CommitteType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.BPMOutComeTypes;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

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

	public QTDetailBackingBean() {
		log.info("inside constructor");

		if (this.getManagedBean().getQt() == null) {
			this.getManagedBean().setQt(new ScQt());
			// this.getManagedBean().getQt().setEvaluationType("2");
		}
		if (this.getManagedBean().getQt().getPmOrgProfile() == null) {
			this.getManagedBean().getQt().setPmOrgProfile(new PmOrgProfile());
		}

		handlePTJName();
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

	private void populateUserMinistryList() {
		debugLogger("populateUserMinistryList");

		PmUser user = new FacesScopes().getUserSessionBean();

		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
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
	 * Add new Quotation/Tender action
	 * 
	 * @return MAINTANANCE_PAGE
	 */
	public String newAction() {
		ScQt scQt = new ScQt();

		scQt.setMinistryOrgProfile(new PmOrgProfile());
		scQt.setDepartmentOrgProfile(new PmOrgProfile());
		// scQt.setPmOrgProfile(orgProfile);
		scQt.setPmOrgProfile(getManagedBean().getOrgProfile());

		String order_id = getAlphaNumeric(8);
		scQt.setQtNo("qt" + order_id);
		System.out.println("SCQT NO : " + scQt.getQtNo());

		scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		populateUserMinistryList();
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderResDTO dto = detailSO
				.retriveQTDetails(new QuationTenderReqDTO());
		this.getManagedBean().setGoList(dto.getGoList());

		this.getManagedBean().setQt(scQt);
		System.out.println("QT NO : " + getManagedBean().getQt().getQtNo());

		getManagedBean().getQt().setEstimatedAmount(new BigDecimal(500000));

		if (getManagedBean().getQt().getEstimatedAmount() != null) {
			if (getManagedBean().getQt().getEstimatedAmount().intValue() > 50000
					&& getManagedBean().getQt().getEstimatedAmount().intValue() <= 500000) {
				getManagedBean().getQt().setPublishPeriod(7);
				// getManagedBean().setPublishingPeriod(7);
				getManagedBean().getQt().setQtValidityPeriod(60);

			} else if (getManagedBean().getQt().getEstimatedAmount().intValue() > 500000) {
				getManagedBean().getQt().setPublishPeriod(21);
				// getManagedBean().setPublishingPeriod(21);
				getManagedBean().getQt().setQtValidityPeriod(90);
			}
		}

		onChangeAction();
		return QTConstants.MAINTANANCE_PAGE + "?faces-redirect=true";
	}

	/**
	 * @param i
	 * @return
	 */
	private String getAlphaNumeric(int len) {
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			int ndx = (int) (Math.random() * getManagedBean().getALPHA_NUM()
					.length());
			sb.append(getManagedBean().getALPHA_NUM().charAt(ndx));
		}
		return sb.toString();
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

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			this.getManagedBean().setUserDivisionList(
					resDTO.getOrgProfileList());
		}
	}

	public void handlePTJNameChanged(PmOrgProfile orgProfile) {
		List<PmOrgProfile> orgProfileList = getManagedBean()
				.getUserDivisionList();
		for (PmOrgProfile list : orgProfileList) {
			if (list.getOrgProfileId() == orgProfile.getOrgProfileId()) {
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
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);

			ScQt selectedScQt = resDTO.getQtDetail();
			
			this.getManagedBean().setGoList(resDTO.getGoList());
			//System.out.println("GOSO List : " + getManagedBean().getGoList().size());

			populateParentOrgProfile(selectedScQt);
			// selectedScQt.setZonal(true);
			selectedScQt.setFileNo(scQt.getQtNo());
			this.getManagedBean().setQt(selectedScQt);
			System.out.println("Is Zonal : "
					+ getManagedBean().getQt().isZonal());
			
			System.out.println("Estimated Price : " + getManagedBean().getQt().getEstimatedAmount());
			
			if (getManagedBean().getQt().getEstimatedAmount() != null) {
				if (getManagedBean().getQt().getEstimatedAmount().intValue() <= 500000) {
					getManagedBean().getQt().setPublishPeriod(7);
					// getManagedBean().setPublishingPeriod(7);
					getManagedBean().getQt().setQtValidityPeriod(60);

				} else if (getManagedBean().getQt().getEstimatedAmount().intValue() > 500000) {
					getManagedBean().getQt().setPublishPeriod(21);
					// getManagedBean().setPublishingPeriod(21);
					getManagedBean().getQt().setQtValidityPeriod(90);
				}
			}
			
			/*
			 * System.out.println(" Status Name : " +
			 * getManagedBean().getQt().getQtStatus().getPmStatusDescs()
			 * .get(0).getStatusName());
			 */
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

		// QT number need to be generated by code it should be unique

		if (scQt.isBrandNew()) {
			scQt.setQtNo(getManagedBean().getQt().getQtNo());
		}
		// No need to set the below fields automatically set by listener
		PmUser users = new FacesScopes().getUserSessionBean();
		scQt.setCreatedBy(users.getUserId());
		scQt.setChangedBy(users.getUserId());
		
		//Setting the Committee Appointment details to Qtdetails
		List<TabBean> tabBeanList = getManagedBean().getScCommitteeMemberTabList();
		if (tabBeanList != null) {
			for (TabBean tabBean : tabBeanList) {
				if (tabBean.getScCommittee().getCommitteeType().equalsIgnoreCase(this.getManagedBean().getTabTitle())) {
					getManagedBean().setScCommittee(tabBean.getScCommittee());
				}
			}

			List<ScCommitteeMember> committeeMembersList = getManagedBean().getScCommitteeMemberList().get(0).getScCommittee().getScCommitteeMembers();
			getManagedBean().getScCommittee().setScCommitteeMembers(committeeMembersList);
			this.getManagedBean().getScQtCommittee().setScCommittee(getManagedBean().getScCommittee());
			List<ScQtCommittee> scQtCommitteeList = new ArrayList<ScQtCommittee>();
			scQtCommitteeList.add(this.getManagedBean().getScQtCommittee());
			scQt.setScQtCommittees(scQtCommitteeList);
		}
		
		// Setting the Business Owner details to Qtdetails
		List<ScQtCommittee> qtCommittees = new ArrayList<ScQtCommittee>();
		qtCommittees.add(this.getManagedBean().getScQtCommittee());
		scQt.setScQtCommittees(qtCommittees);
		scQt.setMinistrySpecSecrataryId(users);

		// Setting the qtBreifing details to Qtdetails
		List<BriefingZoneBean> beansList = getManagedBean()
				.getBriefingZoneBean();
		if (beansList != null) {
			if (scQt.isZonal()) {
				for (BriefingZoneBean briefingZoneBean : beansList) {
					if (briefingZoneBean.getTplSpecZoneDtl().getZoneName().equalsIgnoreCase(
									this.getManagedBean().getBriefingZoneName())) {
						getManagedBean().getScQtBsv().setTplSpecZoneDtl(briefingZoneBean.getTplSpecZoneDtl());
					}
				}
				List<ScQtBsvDtl> briefBeansList = getManagedBean().getBriefingZoneBean().get(0).getBriefingList();
				for (ScQtBsvDtl scQtBsvDtl : briefBeansList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllBriefingReq(getManagedBean().getScQtBsv().isAllBriefingReq());
				}
				getManagedBean().getScQtBsv().setScQtBsvDtls(briefBeansList);

				List<ScQtBsvDtl> siteBeansList = getManagedBean().getBriefingZoneBean().get(0).getSitevisitList();
				for (ScQtBsvDtl scQtBsvDtl : siteBeansList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllBriefingReq(getManagedBean().getScQtBsv().isAllBriefingReq());
				}
				getManagedBean().getScQtBsv().getScQtBsvDtls().addAll(siteBeansList);

				TplSpecZoneDtl tplSpecZoneDtl = (TplSpecZoneDtl) constructRemarks(true);
				getManagedBean().getScQtBsv().setTplSpecZoneDtl(tplSpecZoneDtl);

			} else {
				List<ScQtBsvDtl> selectedBriefList = getManagedBean().getSelectedBriefingDetails();
				for (ScQtBsvDtl scQtBsvDtl : selectedBriefList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllBriefingReq(getManagedBean().getScQtBsv().isAllBriefingReq());
				}
				getManagedBean().getScQtBsv().setScQtBsvDtls(selectedBriefList);

				List<ScQtBsvDtl> selectedSiteVisitList = getManagedBean().getSelectedSiteVisitDetails();
				for (ScQtBsvDtl scQtBsvDtl : selectedSiteVisitList) {
					scQtBsvDtl.setScQtBsv(getManagedBean().getScQtBsv());
					scQtBsvDtl.getScQtBsv().setAllSiteVisitReq(getManagedBean().getScQtBsv().isAllSiteVisitReq());
				}
				getManagedBean().getScQtBsv().getScQtBsvDtls().addAll(selectedSiteVisitList);

				ScQt scQts = (ScQt) constructRemarks(false);
				getManagedBean().getScQtBsv().setScQt(scQts);
			}

			getManagedBean().getScQtBsv().setScQt(getManagedBean().getQt());
			List<ScQtBsv> scQtBsvs = new ArrayList<ScQtBsv>();
			scQtBsvs.add(this.getManagedBean().getScQtBsv());
			scQt.setScQtBriefings(scQtBsvs);
		}
		// Setting the contact person to qtdetails.
		List<ScQtContactPerson> contactPersons = getManagedBean()
				.getSelectedContactPersons();
		if (contactPersons != null && !contactPersons.isEmpty()) {
			List<ScQtContactPerson> contactList = new ArrayList<ScQtContactPerson>();
			List<PmUser> userList = getManagedBean().getPmUsersList();
			ScQtContactPerson contactPerson = getManagedBean()
					.getQtContactPerson();
			for (PmUser pmUser : userList) {
				contactPerson.setUser(pmUser);
				contactList.add(contactPerson);
			}
			contactPersons.addAll(contactList);
			scQt.setScQtContactPersons(contactPersons);
		}

		List<ScQtSampleSubmission> sampleSubmissionList = getManagedBean()
				.getSelectedSubmissionList();
		if (sampleSubmissionList != null && !sampleSubmissionList.isEmpty()) {
			for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
				scQtSampleSubmission.setScQt(getManagedBean().getQt());
			}
			scQt.setScQtSampleSubmission(sampleSubmissionList);
		}
		
		// Setting the Deductions to qtdetails.
		List<ScQtDeduction> deductionList = getManagedBean()
				.getScQtDeductionList();
		if (deductionList != null && !deductionList.isEmpty()) {
			for (ScQtDeduction scQtDeduction : deductionList) {
				// scQtDeduction.setScQt(getManagedBean().getQt());
				/*if (scQtDeduction.getDeductionType().getParameterId()
						.equals("")) {
					scQtDeduction.setDeductionBy("A");
				}*/
				scQtDeduction.setDeductionBy("A");
			}
			scQt.setScQtDeductions(deductionList);
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
	private BaseEntity constructRemarks(boolean isZonal) {
		List<ScRemark> remarks = this.getManagedBean().getRemarks();
		TplSpecZoneDtl specZoneDtl = null;
		ScQt qt = null;
		
		if (isZonal) {
			specZoneDtl = this.getManagedBean().getSelectedZone();
			List<ScQtBreifingZoneRemark> zoneRemarks = new ArrayList<>();
			for (ScRemark scRemark : remarks) {
				ScQtBreifingZoneRemark briefingRemark = (ScQtBreifingZoneRemark) scRemark;
				if (briefingRemark.getRemark() != null
						&& briefingRemark.getRemarkId() == null) 
					briefingRemark.setTplSpecZoneDtl(specZoneDtl);
					zoneRemarks.add(briefingRemark);

				}
			
			specZoneDtl.setQtBriefingRemarks(zoneRemarks);
			return specZoneDtl;
		} else {
			qt = this.getManagedBean().getQt();	
			List<ScQtBriefingRemark> normalRemarks = new ArrayList<>();
			for (ScRemark scRemark : remarks) {
				ScQtBriefingRemark briefingRemark = (ScQtBriefingRemark) scRemark;
				if (briefingRemark.getRemark() != null
						&& briefingRemark.getRemarkId() == null) {
					briefingRemark.setScQt(qt);
					normalRemarks.add(briefingRemark);

				}
			}
			qt.setScQtBriefingRemarks(normalRemarks);
			return qt;
		}
	}

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
			// Need to add status

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void onChangeAction() {

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
		/*
		 * System.out.println("qTDetailBB.managedBean.qt.qtType : " +
		 * getManagedBean().getQt().getQtType()); System.out.println("Panel : "
		 * + getManagedBean().getPanelItem());
		 */
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
		} else if (QTConstants.QT_BUSINESS_OWNER.equalsIgnoreCase(tabTitle)) {
			retrieveBusinessOwnerTabDtl();
		} else if (QTConstants.QT_COMMITTEE_APPOINTMENT
				.equalsIgnoreCase(tabTitle)) {
			retrieveCommitteeAppointmentTabDtl();
		} else if (QTConstants.QT_BRIEFING_SITEVISIT.equalsIgnoreCase(tabTitle)) {
			retrieveBriefingSitevisitTabDtl();
		}

	}

	/**
	 * 
	 */
	private void retrieveBriefingSitevisitTabDtl() {
		debugLogger("retrieveBriefingSitevisitTabDtl");
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
				getManagedBean().setQt(scQt);

				if (getManagedBean().getScQtBsv() == null) {
					getManagedBean().setScQtBsv(new ScQtBsv());
				}

				if (resDTO.getScQtBsv() != null) {

					if (scQt.isZonal()) {
						getManagedBean().setZones(resDTO.getZonesList());
						List<ScQtBsvDtl> briefings = null;
						List<ScQtBsvDtl> siteVisit = null;

						for (TplSpecZoneDtl tplSpecZoneDtl : getManagedBean()
								.getZones()) {
							// if (scQt.isSameBsv()) {
							/*
							 * briefings =
							 * retrieveBriefingSiteVisitDtlsByZoneNQtId(
							 * scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId(),
							 * scQt.getAttendenceType());
							 */

							// } else {
							briefings = retrieveBriefingSiteVisitDtlsByZoneNQtId(
									scQt.getQtId(),
									tplSpecZoneDtl.getSpecZoneId(), "B");

							siteVisit = retrieveBriefingSiteVisitDtlsByZoneNQtId(
									scQt.getQtId(),
									tplSpecZoneDtl.getSpecZoneId(), "S");

							// }
							BriefingZoneBean bean = new BriefingZoneBean(
									tplSpecZoneDtl, briefings, siteVisit);
							getManagedBean().getBriefingZoneBean().add(bean);
						}
						/*
						 * getManagedBean().setSelectedZone(
						 * resDTO.getZonesList().get(0));
						 */

						// getManagedBean().setBriefings(briefings);
						// getManagedBean().setSiteBriefings(siteVisit);

					} else {
						// Briefing Details Listing
						List<ScQtBsvDtl> briefingDetailslist = resDTO
								.getBriefingResultBean().getResultList();
						getManagedBean().setBriefings(briefingDetailslist);

						// Site Visit Listing
						List<ScQtBsvDtl> sitevisitlist = resDTO
								.getSiteVisitResultBean().getResultList();
						getManagedBean().setSiteBriefings(sitevisitlist);
						this.getManagedBean().setRemarks(
								resDTO.getBriefingRemarks());
					}

					initializeRemarks(scQt.isZonal());

					ScQtBsvRegistration scQtBsvRegistration = new ScQtBsvRegistration();
					scQtBsvRegistration.setSupplier(new SmSupplier());
					// scQtBriefingRegistration.setSmMofAccount(new
					// SmMofAccount());
					getManagedBean()
							.setScQtBsvRegistration(scQtBsvRegistration);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, ex
								.toString(), ex.getMessage()));
			} finally {
				qTBriefingSO = null;
			}
		}
	}

	/*
	 * public void bsvZoneTabChange(TabChangeEvent evnt) { ScQt scQt =
	 * getManagedBean().getQt();
	 * 
	 * BriefingZoneBean briefingZoneBean = (BriefingZoneBean) evnt.getData();
	 * 
	 * TplSpecZoneDtl tplSpecZoneDtl = briefingZoneBean.getTplSpecZoneDtl();
	 * getManagedBean().setSelectedZone(tplSpecZoneDtl); if (scQt.isSameBsv()) {
	 * List<ScQtBriefing> briefings = retrieveBriefingSiteVisitDtlsByZoneNQtId(
	 * scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId(),
	 * scQt.getAttendenceType()); this.getManagedBean().setBriefings(briefings);
	 * } else { List<ScQtBriefing> briefings =
	 * retrieveBriefingSiteVisitDtlsByZoneNQtId( scQt.getQtId(),
	 * tplSpecZoneDtl.getSpecZoneId(), "B");
	 * this.getManagedBean().setBriefings(briefings); List<ScQtBriefing>
	 * siteVisit = retrieveBriefingSiteVisitDtlsByZoneNQtId( scQt.getQtId(),
	 * tplSpecZoneDtl.getSpecZoneId(), "S");
	 * 
	 * this.getManagedBean().setSiteBriefings(siteVisit);
	 * 
	 * } // briefingZoneBean.getTplSpecZoneDtl().getZoneName() for
	 * (BriefingZoneBean tplSpecZoneDtls : getManagedBean()
	 * .getBriefingZoneBean()) { if (tplSpecZoneDtls .getTplSpecZoneDtl()
	 * .getZoneName() .equalsIgnoreCase(
	 * briefingZoneBean.getTplSpecZoneDtl().getZoneName())) { briefingZoneBean =
	 * new BriefingZoneBean(tplSpecZoneDtl, getManagedBean().getBriefings(),
	 * getManagedBean() .getSiteBriefings());
	 * getManagedBean().getBriefingZoneBean().remove(tplSpecZoneDtls);
	 * getManagedBean().getBriefingZoneBean().add(briefingZoneBean); break; } }
	 * // }
	 * 
	 * }
	 */

	public void processChecked(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		Boolean id = (Boolean) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		// getManagedBean().setSelectedBoolean(id);
		// this.getManagedBean().getQtCriteria().setAllState(id);
	}

	/**
	 * @param zonal
	 */
	private void initializeRemarks(boolean isZonal) {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScRemark remark = null;
		if (isZonal) {
			remark = new ScQtBreifingZoneRemark();
		} else {
			remark = new ScQtBriefingRemark();
		}
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.QT_BRIEFING_SITE_VISIT);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());
			getManagedBean().getRemarks().add(remark);
		}

	}

	/**
	 * @param qtId
	 * @param specZoneId
	 * @param attendenceType
	 * @return
	 */
	private List<ScQtBsvDtl> retrieveBriefingSiteVisitDtlsByZoneNQtId(
			Long qtId, Long zoneId, String bsvType) {
		IQTBriefingSO qTBriefingSO;

		qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
		BriefingReqDTO reqDTO = new BriefingReqDTO();
		reqDTO.setQtId(qtId);
		reqDTO.setSpecZoneId(zoneId);
		reqDTO.setBriefingType(bsvType);
		BriefingResDTO briefingResDTO = qTBriefingSO
				.retrieveBriefingSiteVisitList(reqDTO);
		this.getManagedBean().setRemarks(briefingResDTO.getBriefingRemarks());
		return briefingResDTO.getBriefingResultBean().getResultList();
	}

	/**
	 * 
	 */
	private void retrieveCommitteeAppointmentTabDtl() {

		if (getManagedBean().getQt().getQtId() != null) {
			System.out.println("Evaluation Type : "
					+ getManagedBean().getQt().getEvaluationType());
			System.out.println("QT ID : " + getManagedBean().getQt().getQtId());
			List<TabBean> tabBeans = new ArrayList<TabBean>();
			// ScCommittee committee = new ScCommittee();

			tabBeans.add(new TabBean("Business Owner", getCommitteeTypeDtl(
					"BO", getManagedBean().getQt().getQtId())));
			tabBeans.add(new TabBean("Specification Committee",
					getCommitteeTypeDtl(CommitteType.getValue("SpecificationCommittee"), getManagedBean().getQt()
							.getQtId())));
			if (this.getManagedBean().getQt().getEvaluationType().equals("2")) {
				tabBeans.add(new TabBean("Opening Committee",
						getCommitteeTypeDtl("OC", getManagedBean().getQt()
								.getQtId())));
				tabBeans.add(new TabBean("TEC",
						getCommitteeTypeDtl("TEC", 146l)));
				tabBeans.add(new TabBean("FEC", getCommitteeTypeDtl("FEC",
						getManagedBean().getQt().getQtId())));
			} else {
				tabBeans.add(new TabBean("Evaluation Committee",
						getCommitteeTypeDtl("EC", getManagedBean().getQt()
								.getQtId())));
			}
			// tabBeans.add(new TabBean("Finalization Secretary", committee));
			if (getManagedBean().getQt().getEstimatedAmount().intValue() > 500000) {

				SelectItem[] selectItems = new SelectItem[2];
				selectItems[0] = new SelectItem("PBB", "PBB");
				selectItems[1] = new SelectItem("PBA", "PBA");

				getManagedBean().setFinalizeComm(selectItems);

				tabBeans.add(new TabBean("Finalization Secretary",
						getFinalizationCommitteeTypeDtl("PBA", null)));
			} else {

				SelectItem[] selectItems = new SelectItem[2];
				selectItems[0] = new SelectItem("QC", "QC");
				selectItems[1] = new SelectItem("QCB", "QCB");

				getManagedBean().setFinalizeComm(selectItems);

				tabBeans.add(new TabBean("Finalization Secretary",
						getFinalizationCommitteeTypeDtl("QC", null)));
			}
			this.getManagedBean().setScCommitteeMemberTabList(tabBeans);
		}
	}

	/**
	 * @param string
	 * @param object
	 * @return
	 */
	private ScCommittee getFinalizationCommitteeTypeDtl(String type, Long id) {
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		ScCommittee committee = null;
		// getManagedBean().setScCommittee(null);
		reqDTO.setCommitteeType(type);
		// reqDTO.setQtId(id);
		reqDTO.setFinalizationSecratary(true);
		// reqDTO.getsetCommitteId(scQtCommittee.getScCommittee().getCommitteeId());
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
						+ committeeMember.getMemberRole() + "$$$"
						+ committeeMember.getScCommittee().getCommitteeType());
			}

			if (!commMembers.isEmpty()) {
				committee = commMembers.get(0).getScCommittee();
				committee.setScCommitteeMembers(commMembers);
			} else {
				committee = new ScCommittee();
				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return committee;
	}

	/**
	 * @return
	 */
	private ScCommittee getCommitteeTypeDtl(String type, Long id) {
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		ScCommittee committee = null;
		// getManagedBean().setScCommittee(null);
		reqDTO.setCommitteeType(type);
		reqDTO.setQtId(id);
		// reqDTO.getsetCommitteId(scQtCommittee.getScCommittee().getCommitteeId());
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
						+ committeeMember.getMemberRole() + "$$$"
						+ committeeMember.getScCommittee().getCommitteeType());
			}

			if (!commMembers.isEmpty()) {
				committee = commMembers.get(0).getScCommittee();
				committee.setScCommitteeMembers(commMembers);
				getManagedBean().setScCommittee(committee);
			} else {
				committee = new ScCommittee();
				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
			}

			/*
			 * if(getManagedBean().getScCommittee().getScCommitteeMembers() ==
			 * null) {
			 * getManagedBean().getScCommittee().setScCommitteeMembers(new
			 * ArrayList<ScCommitteeMember>()); }
			 * getManagedBean().getScCommittee
			 * ().setScCommitteeMembers(commMembers);
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return committee;
	}

	/**
	 * 
	 */
	private void retrieveBusinessOwnerTabDtl() {
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
				System.out.println("Committee Members List : "
						+ committeeMembersList.size());
				getManagedBean().setScCommitteeMemberList(committeeMembersList);
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										ex.toString(),
										"Sorry We are unable to process."));
			} finally {
				committeSO = null;
			}
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
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);

				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();
				System.out.println("Deduction List : " + deductionList.size());
				for (ScQtDeduction scQtDeduction : deductionList) {
					System.out
							.println("PM Parameter ID : "
									+ scQtDeduction.getDeductionType()
											.getParameterId());
					// System.out.println("PM Parameter Name : " +
					// scQtDeduction.getDeductionType().getPmParameterDescs().get(0).getCodeName());
				}

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
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
		// ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();
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
			IQTDetailSO detailSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

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
			IQTDetailSO detailSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

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
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
		// ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);

	}

	private void retrieveApprovalTabDtl() {
		if (this.getManagedBean().getTabList() == null) {
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
								.getDisplayName(qtApproval
										.getApprovalCategory()));
						// qtApproval.setVerifyOption("O");
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

		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());

			QuationTenderResDTO resDTO = detailSO
					.retriveQTSuppQulCriDetails(reqDTO);

			/*
			 * ScQtCriteria scQtCriteria = resDTO.getCriteria(); if
			 * (scQtCriteria == null) { scQtCriteria = new ScQtCriteria(); }
			 * getManagedBean().setQtCriteria(scQtCriteria);
			 */
			getManagedBean().setStates(resDTO.getStates());
			getManagedBean().setCategoryL1s((resDTO.getCategoryL1()));
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), "Sorry We are unable to process."));

		} finally {
			detailSO = null;
		}
	}

	private void retrieveScheduleTabDtl() {
		debugLogger("retrieveScheduleTabDtl");
		if (getManagedBean().getQt().getQtId() != null) {
			System.out.println("Closing Date : "
					+ getManagedBean().getQt().getClosingDate());
			System.out.println("Proposal Validity(days) : "
					+ getManagedBean().getQt().getQtValidityPeriod());

			Calendar cal = Calendar.getInstance();
			cal.setTime(getManagedBean().getQt().getClosingDate());
			cal.add(Calendar.DATE, getManagedBean().getQt()
					.getQtValidityPeriod());
			System.out.println("Date : " + cal.getTime());
			getManagedBean().setProposalValidityUntil(cal.getTime());
			System.out.println("Proposal Validity Until : "
					+ getManagedBean().getProposalValidityUntil());

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
				/*
				 * for (ScQtContactPerson scQtContactPerson : deskOfficer) {
				 * getManagedBean
				 * ().getPmUsersList().add(scQtContactPerson.getUser()); }
				 */
				getManagedBean().setPmuserContactpersonList(deskOfficer);
				System.out.println("UserName : "
						+ getManagedBean().getPmuserContactpersonList().get(0)
								.getUser().getUserName());

				System.out.println("ZOnal Id : "
						+ getManagedBean().getQt().isZonal());
				if (getManagedBean().getQt().isZonal()) {
					// Sample Submission
					List<ScQtSampleSubmission> submissionList = resDTO
							.getSampleSubmissionResultBean().getResultList();

					System.out.println("Sample Submission list: "
							+ submissionList.size());

					getManagedBean()
							.setScQtSampleSubmissionList(submissionList);
					System.out.println("Zone Name : "
							+ getManagedBean().getScQtSampleSubmissionList()
									.get(0).getTplSpecZoneDtl().getZoneName());
					System.out.println("Address : "
							+ getManagedBean().getScQtSampleSubmissionList()
									.get(0).getAddress().getAddress1());
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

	/*
	 * private void retrieveScheduleTabDtl() { if
	 * (getManagedBean().getCountries() == null) { IQTDetailSO detailSO; try {
	 * detailSO = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	 * 
	 * QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
	 * reqDTO.setQtId(getManagedBean().getQt().getQtId());
	 * 
	 * QuationTenderResDTO resDTO = detailSO .retriveQTScheduleDetails(reqDTO);
	 * 
	 * // Briefing List<ScQtBriefing> briefingList = resDTO
	 * .getBriefingResultBean().getResultList();
	 * System.out.println("Briefing List : " + briefingList.size());
	 * getManagedBean().setBriefings(briefingList);
	 * 
	 * // Site Visit List<ScQtBriefing> siteVisitList = resDTO
	 * .getSiteVisitResultBean().getResultList();
	 * System.out.println("Site Visit list: " + siteVisitList.size());
	 * getManagedBean().setSiteBriefings(siteVisitList);
	 * 
	 * // Contact Person List<ScQtContactPerson> contactPersonList = resDTO
	 * .getContactPersonResultBean().getResultList();
	 * System.out.println("Contact Person list: " + contactPersonList.size());
	 * getManagedBean().setScQtContactPersons(contactPersonList);
	 * 
	 * getManagedBean().setCountries(resDTO.getCountries());
	 * 
	 * } catch (Exception ex) { ex.printStackTrace();
	 * FacesContext.getCurrentInstance() .addMessage( null, new
	 * FacesMessage(FacesMessage.SEVERITY_ERROR, ex.toString(),
	 * "Sorry We are unable to process.")); } finally { detailSO = null; } } }
	 */

	private void retrieveDocumentTabDtl() {
		if (getManagedBean().getScQtChecklists() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());
				List<String> type = new ArrayList<String>();
				type.add("UP");
				type.add("TD");
				reqDTO.setCheckListType(type);

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
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(getManagedBean().getQt().getQtId());

				QuationTenderResDTO resDTO = detailSO.retriveQtRemarks(reqDTO);
				/*
				 * List<ScQtRemark> remarkList = resDTO.getRemarkResultBean()
				 * .getResultList(); System.out.println("Remark List : " +
				 * remarkList.size());
				 * getManagedBean().setScQtRemarks(remarkList);
				 */
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
}
