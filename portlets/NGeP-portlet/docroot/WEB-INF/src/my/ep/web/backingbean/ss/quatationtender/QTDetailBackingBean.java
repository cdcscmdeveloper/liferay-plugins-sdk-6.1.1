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
import javax.faces.model.SelectItem;

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
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
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
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.type.ss.quationtender.ItemOrigin;
import my.ep.web.type.ss.quationtender.ItemType;
import my.ep.web.type.ss.quationtender.LineItem;
import my.ep.web.type.ss.quationtender.QTType;
import my.ep.web.type.ss.quationtender.SSProcurmentMode;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);

	private static final String MODULE_NAME = "ss";
	private IQTDetailSO detailSO = null;
	private IObjectSO objectSO = null;
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
		retriveQtDetails();
	}

	/**
	 * Creating the SO after bean object creation. instead of calling from
	 * constructor
	 */
	@PostConstruct
	public void initializeObejcts() {
		ScQt scQt = this.getManagedBean().getQt();

		scQt = new ScQt();
		this.getManagedBean().setQt(scQt);

		populateProcurementModeItems();
		populateItemType();
		populateTenderType();
		populateResponseRule();
		populateItemOrigin();

		log.info("successfully look up the so object" + detailSO);
	}

	/*
	 * Method to load for Qt details screen information
	 */
	public void retriveQtDetails() {
		List<PmGo> goList = this.getManagedBean().getGoList();
		// Make a SO call only once to get the data if it is empty other wise
		// skip the so call
		try {
			QuationTenderResDTO resDto = detailSO
					.retriveQTDetails(new QuationTenderReqDTO());
			if (goList == null || goList.isEmpty()) {

				this.getManagedBean().setGoList(resDto.getGoList());
			}

			List<PmOrgProfile> minstryList = resDto.getMinistryList();
			prepareMinistryList(minstryList);
		} catch (Exception ex) {

		}
	}

	private void prepareMinistryList(List<PmOrgProfile> orgProfile) {
		List<SelectItem> minstryList = new ArrayList<SelectItem>();
		minstryList.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgProfile) {
			minstryList.add(new SelectItem(pmOrgProfile.getOrgProfileId()
					.toString(), pmOrgProfile.getOrgCode()));
		}

		this.getManagedBean().setMinstryList(minstryList);
	}

	/*
	 * To add empty row to gosodo table
	 */
	public void addNewGoSoDoItem() {
		log.info("begin add gosodo item method");
		List<ScQtGosodoBudget> gosodos = this.getManagedBean().getQt()
				.getScQtGosodoBudgets();
		if (gosodos == null) {
			gosodos = new ArrayList<ScQtGosodoBudget>();

		} 
		/*else {
			ScQtGosodoBudget budget = new ScQtGosodoBudget();
			gosodos.add(budget);
		}*/
		ScQtGosodoBudget budget = new ScQtGosodoBudget();
		gosodos.add(budget);
		this.getManagedBean().getQt().setScQtGosodoBudgets(gosodos);
		log.info("End of add gosodo item method");
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
	public void handleGoChange(ValueChangeEvent changeEvent) {
		log.info("inside handle go change method");
		PmGo pmGo = (PmGo) changeEvent.getNewValue();
		if (pmGo != null && pmGo.getGoId() != null) {
			// get the so list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setGoId(pmGo.getGoId());
			ObjectResDTO objResDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			List<PmSo> oldsolist = this.getManagedBean().getSoList();
			oldsolist.clear();
			this.getManagedBean().setSoList(objResDTO.getPmSoList());
		}
		log.info("end of handle go change method");
	}

	/*
	 * OnChange of So's get the do's list
	 */
	public void handleSoChange(ValueChangeEvent changeEvent) {
		log.info("inside handle so change method");
		PmSo pmSo = (PmSo) changeEvent.getNewValue();
		if (pmSo != null && pmSo.getSoId() != null) {
			// get the do list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setGoId(pmSo.getSoId());
			ObjectResDTO objResDTO = objectSO.retrieveDOListBySOCode(reqDTO);
			List<PmDo> olddolist = this.getManagedBean().getDoList();
			olddolist.clear();
			this.getManagedBean().setDoList(objResDTO.getPmDoList());
		}
		log.info("end  of handle go change method");
	}

	/**
	 * Method to save the QT details
	 */
	public void saveQtDetails() {

		QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();

		try {
			QuationTenderResDTO quationTenderResDTO = detailSO
					.saveOrUpdateQT(quationTenderReqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));

		}
	}

	/**
	 * Method to search the quationtenders based on serach date
	 */
	public void searchQT() {

		try {
			List<ScQt> searchQt = getSearchQTs();

			if (searchQt != null && !searchQt.isEmpty()) {
				this.getManagedBean().getQtList().addAll(searchQt);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method for clone search qts
	 */

	public void cloneSearchQt() {
		try {
			List<ScQt> cloneSearchQt = getSearchQTs();
			if (cloneSearchQt != null && !cloneSearchQt.isEmpty()) {
				
				this.getManagedBean().getCloneTenderList()
						.addAll(cloneSearchQt);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * To clear the clone search result for next time
	 */
	public void clearCloneQT() {
		System.out.println("inside clear cloneQT-------------------");
		List<ScQt> cloneQtList = this.getManagedBean().getCloneTenderList();
		if (!cloneQtList.isEmpty()) {
			this.getManagedBean().getCloneTenderList().clear();
		}

	}

	private void populateTenderType() {
		List<SelectItem> tenderTypeList = new ArrayList<SelectItem>();
		tenderTypeList.add(new SelectItem("", "Select One"));
		QTType[] tenderTypes = QTType.values();
		for (QTType tenderType : tenderTypes) {
			tenderTypeList.add(new SelectItem(tenderType.value(), tenderType
					.name()));
		}
		this.getManagedBean().setTenderTypeList(tenderTypeList);

	}

	private void populateItemType() {
		List<SelectItem> itemTypeList = new ArrayList<SelectItem>();
		itemTypeList.add(new SelectItem("", "Select One"));
		ItemType[] itemTypes = ItemType.values();
		for (ItemType itemType : itemTypes) {
			itemTypeList.add(new SelectItem(itemType.value(), itemType.name()));
		}
		this.getManagedBean().setItemTypeList(itemTypeList);

	}

	private void populateItemOrigin() {
		List<SelectItem> itemOriginList = new ArrayList<SelectItem>();
		itemOriginList.add(new SelectItem("", "Select One"));
		ItemOrigin[] itemOrigins = ItemOrigin.values();
		for (ItemOrigin itemOrigin : itemOrigins) {
			itemOriginList.add(new SelectItem(itemOrigin.value(), itemOrigin
					.name()));
		}
		this.getManagedBean().setItemOriginList(itemOriginList);
	}

	private void populateResponseRule() {
		List<SelectItem> lineItemList = new ArrayList<SelectItem>();
		lineItemList.add(new SelectItem("", "Select One"));
		LineItem[] responseRules = LineItem.values();
		for (LineItem responseRule : responseRules) {
			lineItemList.add(new SelectItem(responseRule.value(), responseRule
					.name()));
		}
		this.getManagedBean().setLineItemList(lineItemList);
	}

	private void populateProcurementModeItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("", "Select One"));
		SSProcurmentMode[] procuementModes = SSProcurmentMode.values();
		for (SSProcurmentMode mode : procuementModes) {
			items.add(new SelectItem(mode.value(), mode.name()));
		}
		this.getManagedBean().setProcurementModeItems(items);
	}

	/**
	 * Method to return the maintance page
	 * 
	 * @return maintanacepage
	 */
	public String showMaintanceQT(Long qtId) {

		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtDetailId(qtId);
		QuationTenderResDTO resDTO = detailSO.retriveQtById(reqDTO);

		this.getManagedBean().setQt(resDTO.getQtDetail());

		return QTConstants.MAINTANANCE_PAGE;
	}

	/**
	 * Method to prepare scqt object with all dependent object.
	 * 
	 * @return QTDetailReq object
	 */
	private QuationTenderReqDTO prepareQTInfo() {

		ScQt scQt = this.getManagedBean().getQt();

		// TODO QT number need to be generated by code it should be unique
		scQt.setQtNo("SC031230");

		// No need to set the below fields automatically set by listener
		scQt.setCreatedBy(1l);
		scQt.setChangedBy(1l);
		scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		// TODO need to get the org profile id by user id and need to set
		OrgProfileReqDTO orgUnitReqDTO = new OrgProfileReqDTO();
		String divisonCode = getManagedBean().getDivisionCode();
		Long profileId = -1l;
		if (divisonCode != null && !"".equals(divisonCode)) {
			profileId = new Long(divisonCode);
			orgUnitReqDTO.setOrgProfileId(profileId);
		}

		OrgProfileResDTO unitResDTO = orgUnitSO
				.retrieveOrgProfileByProfileId(orgUnitReqDTO);
		PmOrgProfile orgProfile = unitResDTO.getPmOrgProfile();
		scQt.setPmOrgProfile(orgProfile);
		// Setting the pm status for qt details
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(1l);
		StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
		PmStatus status = pmStatus.getPmStatus();
		scQt.setPmStatus(status);
		// Setting the gosodoBudget to qtdetails
		// TODO need to get it from managed bean and set it.
		List<ScQtGosodoBudget> bugets = null;
		scQt.setScQtGosodoBudgets(bugets);
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
		List<ScQtCriteria> criterias = null;
		scQt.setScQtCriterias(criterias);
		// Setting the approvalist to Qtdetails
		// TODO Need to get it from managed bean and set it.
		List<ScQtApproval> qtApprovals = null;
		scQt.setScQtApprovals(qtApprovals);
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

		return quationTenderReqDTO;

	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @return quatationTenderReq object
	 */
	private List<ScQt> getSearchQTs() {

		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		ScQt qt = getManagedBean().getQt();

		if (qt != null) {

			SearchBean<ScQt> qtSearchBean = new SearchBean<ScQt>();
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qt.getQtTitle();
			if (null != qtTitle) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_TITILE);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtTitle);
				searchFilterBean.add(bean);
			}
			String qtNumber = qt.getQtNo();
			if (null != qtNumber) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			if (null != qt.getProcurementMode()) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField(QTConstants.PROCUREMENT_MODE);
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(qt.getProcurementMode());
				searchFilterBean.add(bean);
			}
			if (null != qt.getCreateDateFrom() || qt.getCreatedDateTo() != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());

				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						qt.getCreateDateFrom(), qt.getCreatedDateTo());

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			if (null != qt.getPubDateFrom() || null != qt.getPubDateTo()) {
				SearchFilterBean bean = new SearchFilterBean();

				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						qt.getPubDateFrom(), qt.getPubDateTo());

				bean.setSearchValue(betweenVal);

				searchFilterBean.add(bean);
			}

			if (null != qt.getClsDateFrom() || null != qt.getClsDateTo()) {
				SearchFilterBean bean = new SearchFilterBean();

				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						qt.getClsDateFrom(), qt.getClsDateTo());

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
        //After Change Need To Remove
		return dto.getQtResultBean().getResultList();
		//return  null != dto.getQtResultBean().getResultList() ? dto.getQtResultBean().getResultList() : new ArrayList<ScQt>();
	}

	public void fetchDepartements() {
		System.out.println("**************inside fetchDepartments"
				+ getManagedBean().getMinstryCode());
		String minstrycode = getManagedBean().getMinstryCode();
		if (null == minstrycode || "".equals(minstrycode)) {
			throw new IllegalArgumentException();
		} else {
			Long parentId = new Long(minstrycode);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
			reqDTO.setParentOrgProfileId(parentId);
			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			prepareDeptList(resDTO.getOrgProfileList());
		}
	}

	private void prepareDeptList(List<PmOrgProfile> orgprofiles) {
		List<SelectItem> deptcodes = new ArrayList<SelectItem>();
		deptcodes.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgprofiles) {
			deptcodes.add(new SelectItem(pmOrgProfile.getOrgProfileId()
					.toString(), pmOrgProfile.getOrgCode()));
		}
		this.getManagedBean().setDeptList(deptcodes);
	}

	public void fetchDivisions() {
		System.out.println("**************inside fetchDepartments"
				+ getManagedBean().getMinstryCode());
		String minstrycode = getManagedBean().getDeptCode();
		if (null == minstrycode || "".equals(minstrycode)) {
			throw new IllegalArgumentException();
		} else {
			Long parentId = new Long(minstrycode);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(parentId);
			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			preparedDivisions(resDTO.getOrgProfileList());
		}
	}

	private void preparedDivisions(List<PmOrgProfile> orgprofiles) {
		List<SelectItem> devisions = new ArrayList<SelectItem>();
		devisions.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgprofiles) {
			devisions.add(new SelectItem(pmOrgProfile.getOrgProfileId()
					.toString(), pmOrgProfile.getOrgCode()));
		}
		this.getManagedBean().setDivisionList(devisions);
	}

}
