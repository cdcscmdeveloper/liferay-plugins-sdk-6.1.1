package my.ep.web.backingbean.ss.quatationtender;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
import my.ep.dto.pm.common.categorys.CategorysReqDTO;
import my.ep.dto.pm.common.categorys.CategorysResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
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
import my.ep.type.pp.ProcurementMode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.type.ss.quationtender.ItemOrigin;
import my.ep.web.type.ss.quationtender.ItemType;
import my.ep.web.type.ss.quationtender.LineItem;
import my.ep.web.type.ss.quationtender.QTType;
import my.ep.web.type.ss.quationtender.RecordStatus;
import my.ep.web.type.ss.quationtender.SSProcurmentMode;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.python.modules.newmodule;

import weblogic.management.j2ee.statistics.TimeStat;

/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends BaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);

	private static final String MODULE_NAME = "ss";
	private IQTDetailSO detailSO = null;
	private IObjectSO objectSO = null;
	private ICategorysSO categorysSO = null;
	private IStateSO stateSo = null;
	private IDistrictSO districtSO = null;
	private IOrgUnitSO orgUnitSO = null;
	private IStatusSO statusSO = null;

	public QTDetailBackingBean() {

		log.info("inside constructor");

	}

	/**
	 * Creating the SO after bean object creation. instead of calling from
	 * constructor
	 */
	@PostConstruct
	public void initializeObejcts() {
		ScQt scQt = this.getManagedBean().getQt();
		if (null == scQt) {
			scQt = new ScQt();
			this.getManagedBean().setQt(scQt);
		}
		populateProcurementModeItems();
		populateItemType();
		populateTenderType();
		populateResponseRule();
		populateItemOrigin();

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, IQTDetailSO.class);
		IObjectSO objectSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IObjectSO.class);
		ICategorysSO categorysSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", ICategorysSO.class);

		IStateSO stateSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStateSO.class);
		IDistrictSO districtSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);

		IOrgUnitSO orgUnitSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IOrgUnitSO.class);

		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStatusSO.class);

		this.detailSO = detailSO;
		this.objectSO = objectSO;
		this.categorysSO = categorysSO;
		this.stateSo = stateSO;
		this.districtSO = districtSO;
		this.orgUnitSO = orgUnitSO;
		this.statusSO = statusSO;
		log.info("successfully look up the so object" + detailSO);
	}

	/*
	 * Method to load for Qt details screen information
	 */
	public void retriveQtDetails() {
		List<PmGo> goList = this.getManagedBean().getGoList();
		// Make a SO call only once to get the data if it is empty other wise
		// skip the so call
		if (goList == null || goList.isEmpty()) {
			ObjectResDTO resDTO = objectSO.retrieveGOList();
			this.getManagedBean().setGoList(resDTO.getPmGoList());
		}
	}

	/*
	 * Method to load the categorieslist for suppliers screen
	 */
	public void retriveQtCriteria() {

		// Retrive categoryL1 and set to managedbean for firsttime other wise
		// skip
		List<PmCategoryL1> l1categoryList = this.getManagedBean()
				.getCategoryL1s();
		if (l1categoryList == null || !l1categoryList.isEmpty()) {
			CategorysResDTO catResDTO = categorysSO.retiveCategoryL1List();
			if (catResDTO.getCategoryL1List() != null
					&& !catResDTO.getCategoryL1List().isEmpty()) {
				this.getManagedBean().setCategoryL1s(
						catResDTO.getCategoryL1List());
			}
			// Retrive states and set to managedbean for firsttime otherwise
			// skip
			List<PmState> stateList = this.getManagedBean().getStates();
			if (stateList == null || !stateList.isEmpty()) {
				StateReqDTO reqDTO = new StateReqDTO();
				reqDTO.setCountryId(new Long(1));
				StateResDTO resDTO = stateSo
						.retriveStatesListByCountryID(reqDTO);
				List<PmState> states = resDTO.getStateList();
				if (states != null && !states.isEmpty()) {
					this.getManagedBean().setStates(states);
				}
			}
		}
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

		} else {
			ScQtGosodoBudget budget = new ScQtGosodoBudget();
			gosodos.add(budget);
		}
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
	 * To add empty row to category code data table
	 */
	public void addNewCategoryItem() {
		List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();
		if (categories == null) {
			categories = new ArrayList<ScQtCategory>();

		} else {
			ScQtCategory category = new ScQtCategory();
			categories.add(category);
		}
		this.getManagedBean().getQt().setScQtCategorys(categories);
	}

	/*
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCode(ValueChangeEvent changeEvent) {
		ScQtCategory category = (ScQtCategory) changeEvent.getNewValue();
		List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();
		if (categories.contains(category)) {
			categories.remove(category);
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

	/*
	 * Method to call onchange of L1category
	 */
	public void handleL2CategoryChange(ValueChangeEvent changeEvent) {
		PmCategoryL1 categoryL1 = (PmCategoryL1) changeEvent.getNewValue();
		if (null != categoryL1 && categoryL1.getCategoryL1Id() != null) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL1CategoryId(categoryL1.getCategoryL1Id());
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL2ListByL1CategoryId(reqDTO);
			List<PmCategoryL2> catL2List = categorysResDTO.getCategoryL2List();
			this.getManagedBean().getCategoryL2s().clear();
			this.getManagedBean().setCategoryL2s(catL2List);
		}
	}

	/*
	 * Method to call onchange of L2category
	 */
	public void handleL3CategoryChange(ValueChangeEvent changeEvent) {

		PmCategoryL2 categoryL2 = (PmCategoryL2) changeEvent.getNewValue();
		if (null != categoryL2 && categoryL2.getCategoryL2Id() != null) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL2CategoryId(categoryL2.getCategoryL2Id());
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL3ListByL2CategoryId(reqDTO);
			List<PmCategoryL3> catL3List = categorysResDTO.getCategoryL3List();
			this.getManagedBean().getCategoryL3s().clear();
			this.getManagedBean().setCategoryL3s(catL3List);
		}
	}

	/*
	 * To retrive districts based on state value changes.
	 */
	public void handleStateChange(ValueChangeEvent changeEvent) {
		PmState state = (PmState) changeEvent.getNewValue();
		if (state != null && state.getStateId() != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(state.getStateId());
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);
			if (resDTO != null && !resDTO.getDistrictList().isEmpty()) {
				this.getManagedBean().getDistricts().clear();
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}
		}
	}

	/**
	 * Method to save the QT details
	 */
	public void saveQtDetails() {

		ScQt scQt = this.getManagedBean().getQt();

		// TODO QT number need to be generated by code it should be unique
		scQt.setQtNo("SC031230");

		// No need to set the below fields automatically set by listener
		scQt.setCreatedBy(1l);
		scQt.setChangedBy(1l);
		scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		// TODO need to get the org profile id by user id and need to set
		OrgUnitReqDTO orgUnitReqDTO = new OrgUnitReqDTO();
		orgUnitReqDTO.setOrgProfileId(new Long(1));
		OrgUnitResDTO unitResDTO = orgUnitSO
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
		System.out
				.println("$$$$$$$$$$$$$$$$inside search qt method$$$$$$$$$$$$$$");
		ScQt qt = getManagedBean().getQt();
		if (qt != null) {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			SearchBean<ScQt> qtSearchBean = new SearchBean<ScQt>();
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtNumber = qt.getQtNo();
			if (null != qtNumber) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("qtNo");
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			if (null != qt.getProcurementMode()) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField("procurementMode");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(qt.getProcurementMode());
				searchFilterBean.add(bean);
			}
			if (null != qt.getCreateDateFrom() || qt.getCreatedDateTo() != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("createdDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());

				SearchBetweenValue betweenVal = new SearchBetweenValue(
						qt.getCreateDateFrom(), qt.getCreatedDateTo());

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			if (null != qt.getPubDateFrom() || null != qt.getPubDateTo()) {
				SearchFilterBean bean = new SearchFilterBean();

				bean.setSearchField("publishDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue betweenVal = new SearchBetweenValue(
						qt.getPubDateFrom(), qt.getPubDateTo());

				bean.setSearchValue(betweenVal);

				searchFilterBean.add(bean);
			}

			if (null != qt.getClsDateFrom() || null != qt.getClsDateTo()) {
				SearchFilterBean bean = new SearchFilterBean();

				bean.setSearchField("closingDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue betweenVal = new SearchBetweenValue(
						qt.getClsDateFrom(), qt.getClsDateTo());

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			// TODO Need to add status

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);

			reqDTO.setQtSearchBean(qtSearchBean);
			QuationTenderResDTO dto = null;
			try {
				dto = detailSO.getQuationTenders(reqDTO);
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, ex
								.toString(), ex.getMessage()));

			}
			List<ScQt> qtList = this.getManagedBean().getQtList();
			if (!qtList.isEmpty()) {
				qtList.clear();
			}
			if (dto != null && dto.getQtResultBean() != null) {
				this.getManagedBean().getQtList()
						.addAll(dto.getQtResultBean().getResultList());
			}
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

}
