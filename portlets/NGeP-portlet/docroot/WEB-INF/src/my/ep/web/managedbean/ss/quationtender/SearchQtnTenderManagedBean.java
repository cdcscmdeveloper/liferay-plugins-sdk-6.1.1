package my.ep.web.managedbean.ss.quationtender;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ss.quationtender.ScAttachment;
import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.type.ss.ApprovalCategory;
import my.ep.web.bean.ss.quatationtender.CategoryCode;
import my.ep.web.bean.ss.quatationtender.GoSoDo;
import my.ep.web.bean.ss.quatationtender.Location;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
import my.ep.web.bean.ss.quatationtender.QtnTenderDataBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "searchQtnTenderMB")
@SessionScoped
public class SearchQtnTenderManagedBean extends BaseManagedBean {

	/**
	 * 
	 */

	private List<String> selectOptions;

	private String selectOption;

	private QtnTenderDataBean dataBean;

	private List<String> supplierList;

	public Map<String, TabBean> getTabview() {
		return tabview;
	}

	public void setTabview(Map<String, TabBean> tabview) {
		this.tabview = tabview;
	}

	private String tenderSelction;

	private static final long serialVersionUID = 1L;

	private List<QtnTenderBean> qtnTenders;
	private QtnTenderBean selectableBean;

	private List<GoSoDo> goSodo;

	private List<Location> locations;
	private List<Location> zoneList;

	private String qtno;
	private Date tenderBean = new Date();

	private Date date;

	private boolean checked;

	private List<SelectItem> stateList;

	private Map<String, List<SelectItem>> cities;

	private List<String> location;

	private String state;

	private boolean selectedBoolean;

	private String selectedBoolean1;

	private List<String> selectedOptions;

	private Map<String, TabBean> tabview;

	private List<TabBean> tabBean;

	private List<SelectItem> checkBoxList;
	private String checkBoxBPK;
	private List<String> checkBoxBPK1;

	private String briefDetails;

	private String visitDetails;
	private ScQtApproval scQtApproval = new ScQtApproval();

	private List<String> approvalTypelist = new ArrayList<String>();

	List<TabBean> tabList = new ArrayList<TabBean>();

	private String approvalAction;
	private String approvalDate;
	private String verifyDate;
	private String standardDocument;
	private String verifyAction;
	private String approverName;
	private String approverstatus;
	private String verifierName;
	private String verifierStatus;
	private ScAttachment scAttachment = new ScAttachment();
	private List<ScQtApproval>	sampleList=new ArrayList<ScQtApproval>();
	/*
	 * public Map<String, List<SelectItem>> getTabview() { return tabview; }
	 * 
	 * public void setTabview(Map<String, List<SelectItem>> tabview) {
	 * this.tabview = tabview; }
	 */
	public List<String> getCheckBoxBPK1() {
		return checkBoxBPK1;
	}

	public void setCheckBoxBPK1(List<String> checkBoxBPK1) {
		this.checkBoxBPK1 = checkBoxBPK1;
	}

	public String getCheckBoxBPK() {
		return checkBoxBPK;
	}

	public void setCheckBoxBPK(String checkBoxBPK) {
		this.checkBoxBPK = checkBoxBPK;
	}

	public List<SelectItem> getCheckBoxList() {
		return checkBoxList;
	}

	public String getSelectedBoolean1() {
		return selectedBoolean1;
	}

	public void setSelectedBoolean1(String selectedBoolean1) {
		this.selectedBoolean1 = selectedBoolean1;
	}

	public void setCheckBoxList(List<SelectItem> checkBoxList) {
		this.checkBoxList = checkBoxList;
	}

	public List<TabBean> getTabBean() {
		return tabBean;
	}

	public void setTabBean(List<TabBean> tabBean) {
		this.tabBean = tabBean;
	}

	public List<String> getSelectedOptions() {
		return selectedOptions;
	}

	public void setSelectedOptions(List<String> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	public boolean isSelectedBoolean() {
		return selectedBoolean;
	}

	public void setSelectedBoolean(boolean selectedBoolean) {
		this.selectedBoolean = selectedBoolean;
	}

	private String zone;

	private String zoneSt;

	private List<String> zoneCt;

	private List<CategoryCode> categories;

	private CategoryCode categoryCode;

	private List<String> mainCcode;

	private List<String> subCcode;

	private List<String> sucCate;

	private List<SelectItem> zones;

	private Map<String, List<SelectItem>> zoneState;

	private Map<String, List<SelectItem>> zoneCities;

	public SearchQtnTenderManagedBean() {
		locations = new ArrayList<Location>();
		zoneList = new ArrayList<Location>();
		qtnTenders = populateSearchQtnBean();
		dataBean = new QtnTenderDataBean(qtnTenders);
		categories = populateCategory();

		mainCcode = populateMainCodes();
		subCcode = populateSubCodes();
		sucCate = populateSucCate();
		goSodo = populateGosodo();
		stateList = new ArrayList<SelectItem>();

		cities = new HashMap<String, List<SelectItem>>();
		stateList.add(new SelectItem("Maine"));
		stateList.add(new SelectItem("South Carolina"));
		stateList.add(new SelectItem("Illinois"));

		List<SelectItem> mcities = new ArrayList<SelectItem>();
		mcities.add(new SelectItem("Augusta"));
		mcities.add(new SelectItem("Bangor"));
		mcities.add(new SelectItem("Bath"));
		mcities.add(new SelectItem("Brunswick"));
		mcities.add(new SelectItem("Castine"));
		mcities.add(new SelectItem("Ellsworth"));
		mcities.add(new SelectItem("Portland"));
		mcities.add(new SelectItem("Woolwich"));
		cities.put("Maine", mcities);

		List<SelectItem> scities = new ArrayList<SelectItem>();
		scities.add(new SelectItem("Charleston"));
		scities.add(new SelectItem("Clemson"));
		scities.add(new SelectItem("Columbia"));
		scities.add(new SelectItem("Greenville"));
		scities.add(new SelectItem("Simpsonville"));
		cities.put("South Carolina", scities);

		List<SelectItem> icities = new ArrayList<SelectItem>();
		icities.add(new SelectItem("Argonne"));
		icities.add(new SelectItem("Batavia"));
		icities.add(new SelectItem("Chicago"));
		icities.add(new SelectItem("Evanston"));
		cities.put("Illinois", icities);

		tabview = new HashMap<>();

		/*
		 * tabview.put("BudgetApproval", new TabBean("Budget Approval"));
		 * tabview.put("BPKApproval", new TabBean("BPKApproval"));
		 * tabview.put("PMSabah", new TabBean("PMSabah"));
		 * tabview.put("PMSarawak", new TabBean("PMSarawak"));
		 * tabview.put("MITIApproval", new TabBean("MITIApproval"));
		 * tabview.put("ICTApproval", new TabBean("ICTApproval"));
		 * tabview.put("PBApproval", new TabBean("PBApproval"));
		 * tabview.put("PTJApproval", new TabBean("PTJApproval"));
		 */
		checkBoxList = new ArrayList();
		checkBoxList.add(new SelectItem("BudgetApproval", "BudgetApproval"));
		checkBoxList.add(new SelectItem("BPKApproval", "BPKApproval"));
		checkBoxList.add(new SelectItem("PMSabah", "PMSabah"));
		checkBoxList.add(new SelectItem("PMSarawak", "PMSarawak"));
		checkBoxList.add(new SelectItem("MITIApproval", "MITIApproval"));
		checkBoxList.add(new SelectItem("ICTApproval", "ICTApproval"));
		checkBoxList.add(new SelectItem("PBApproval",
				"ProcurementBoardApproval"));
		checkBoxList.add(new SelectItem("PTJApproval", "PTJApproval"));

		zones = new ArrayList<SelectItem>();
		zoneState = new HashMap<String, List<SelectItem>>();
		zoneCities = new HashMap<String, List<SelectItem>>();
		zones.add(new SelectItem("South"));
		zones.add(new SelectItem("North"));
		zones.add(new SelectItem("East"));

		List<SelectItem> mstates = new ArrayList<SelectItem>();
		mstates.add(new SelectItem("Maine"));
		mstates.add(new SelectItem("South Carolina"));
		mstates.add(new SelectItem("Illinois"));
		zoneState.put("SOUTH", mstates);
		List<SelectItem> istates = new ArrayList<SelectItem>();
		istates.add(new SelectItem("Maine"));
		istates.add(new SelectItem("South Carolina"));
		istates.add(new SelectItem("Illinois"));
		zoneState.put("NORTH", istates);
		List<SelectItem> cstates = new ArrayList<SelectItem>();
		cstates.add(new SelectItem("Maine"));
		cstates.add(new SelectItem("South Carolina"));
		cstates.add(new SelectItem("Illinois"));
		zoneState.put("EAST", cstates);
		List<SelectItem> kcities = new ArrayList<SelectItem>();
		kcities.add(new SelectItem("Augusta"));
		kcities.add(new SelectItem("Bangor"));
		kcities.add(new SelectItem("Bath"));
		kcities.add(new SelectItem("Brunswick"));
		kcities.add(new SelectItem("Castine"));
		kcities.add(new SelectItem("Ellsworth"));
		kcities.add(new SelectItem("Portland"));
		kcities.add(new SelectItem("Woolwich"));
		zoneCities.put("Maine", kcities);

		List<SelectItem> lcities = new ArrayList<SelectItem>();
		lcities.add(new SelectItem("Charleston"));
		lcities.add(new SelectItem("Clemson"));
		lcities.add(new SelectItem("Columbia"));
		lcities.add(new SelectItem("Greenville"));
		lcities.add(new SelectItem("Simpsonville"));
		zoneCities.put("South Carolina", lcities);

		List<SelectItem> jcities = new ArrayList<SelectItem>();
		jcities.add(new SelectItem("Argonne"));
		jcities.add(new SelectItem("Batavia"));
		jcities.add(new SelectItem("Chicago"));
		jcities.add(new SelectItem("Evanston"));
		zoneCities.put("Illinois", jcities);

		// TODO Tabview start

		ScQtApproval scQtApproval = new ScQtApproval();
		scQtApproval.setApprovalCategory(ApprovalCategory.BUDGET_APPROVAL.value());
		scQtApproval.setVerifyOption("1");
		tabList.add(new TabBean("Budget Approval", scQtApproval));
		
		scQtApproval = new ScQtApproval();
		scQtApproval.setApprovalCategory(ApprovalCategory.BPK_APPROVAL.value());
		scQtApproval.setVerifyOption("1");
		tabList.add(new TabBean("BPK Approval", scQtApproval));
		
		scQtApproval = new ScQtApproval();
		scQtApproval.setApprovalCategory(ApprovalCategory.MITI_APPROVAL.value());
		scQtApproval.setVerifyOption("1");
		//tabList.add(new TabBean("MITI Approval", scQtApproval));
		
		scQtApproval = new ScQtApproval();
		scQtApproval.setApprovalCategory(ApprovalCategory.ICT_APPROVAL.value());
		scQtApproval.setVerifyOption("1");
		tabList.add(new TabBean("ICT Approval", scQtApproval));
		
		scQtApproval = new ScQtApproval();
		scQtApproval.setApprovalCategory(ApprovalCategory.PROCUREMENT_BOARD_APPROVAL.value());
		scQtApproval.setVerifyOption("1");
		//tabList.add(new TabBean("Procurement Board Approval", scQtApproval));
		
		scQtApproval = new ScQtApproval();
		scQtApproval.setApprovalCategory(ApprovalCategory.DIVISION_APPROVAL.value());
		scQtApproval.setVerifyOption("1");
		tabList.add(new TabBean("Division Approval", scQtApproval));
		// Tabview end
		
	}

	private List<String> populateSucCate() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> populateSubCodes() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> populateMainCodes() {
		List<String> mainCodes = new ArrayList();
		mainCodes.add("11");
		mainCodes.add("22");

		return mainCodes;
	}

	private List<CategoryCode> populateCategory() {
		List<CategoryCode> ccodes = new ArrayList();
		CategoryCode categoryCode = new CategoryCode();
		categoryCode.setNum(1);
		categoryCode.setMainCCode("11");
		categoryCode.setSubCCode("22");
		categoryCode.setSubCDevison("33");
		categoryCode.setManditory(true);
		ccodes.add(categoryCode);
		CategoryCode _categoryCode = new CategoryCode();
		_categoryCode.setNum(2);
		_categoryCode.setMainCCode("44");
		_categoryCode.setSubCCode("55");
		_categoryCode.setSubCDevison("66");
		_categoryCode.setManditory(true);
		ccodes.add(_categoryCode);
		return ccodes;
	}

	public String addAction() {

		Location order = new Location(state, location);

		locations.add(order);
		return null;
	}

	public String addZoneAction() {

		Location order = new Location(zone, zoneSt, zoneCt);

		zoneList.add(order);
		return null;
	}

	private List<GoSoDo> populateGosodo() {
		List<GoSoDo> gsdList = new ArrayList<GoSoDo>();
		GoSoDo goSoDo = new GoSoDo();
		goSoDo.setBdgAvail("Yes");
		goSoDo.setType("Test");
		gsdList.add(goSoDo);
		GoSoDo goSoDo1 = new GoSoDo();
		goSoDo1.setBdgAvail("No");
		goSoDo1.setType("User");
		gsdList.add(goSoDo1);

		return gsdList;
	}

	private List<QtnTenderBean> populateSearchQtnBean() {
		List<QtnTenderBean> qtnTenderList = new ArrayList<QtnTenderBean>();
		QtnTenderBean qtnTender = new QtnTenderBean();
		qtnTender.setClsDateFrom(new Date());
		qtnTender.setClsDateTo(new Date());
		qtnTender.setPubDateFrom(new Date());
		qtnTender.setPubDateTo(new Date());
		qtnTender.setCrtDateFrom(new Date());
		qtnTender.setCrtdDateTo(new Date());
		qtnTender.setQtnTenderNum("TN0001");
		qtnTender.setTitle("TENDER DETAILS");

		qtnTender.setProcumentMode("Tender");

		qtnTender.setStatus("Active");
		qtnTenderList.add(qtnTender);
		return qtnTenderList;
	}

	/**
	 * @return the qtnTenders
	 */
	public List<QtnTenderBean> getQtnTenders() {
		return qtnTenders;
	}

	/**
	 * @param qtnTenders
	 *            the qtnTenders to set
	 */
	public void setQtnTenders(List<QtnTenderBean> qtnTenders) {
		this.qtnTenders = qtnTenders;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked
	 *            the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	public List<SelectItem> getCities() {
		if (state != null) {
			return cities.get(state);
		} else {
			return new ArrayList<SelectItem>();
		}
	}

	/**
	 * This listener cleans up the city value if a new state is selected.
	 * 
	 * @param event
	 *            a change event when the value of the state changes.
	 */
	public void stateChangeListener(ValueChangeEvent event) {
		if (event.getNewValue() != state) {
			location = null;
		}
	}

	public void zoneChangeListener(ValueChangeEvent event) {
		if (event.getNewValue() != zone) {
			zoneSt = null;
		}
	}

	public void zoneStChangeListener(ValueChangeEvent event) {
		if (event.getNewValue() != zoneSt) {
			zoneCt = null;
		}
	}

	/**
	 * @return the stateList
	 */
	public List<SelectItem> getStateList() {
		return stateList;
	}

	/**
	 * 
	 * @param stateList
	 *            the stateList to set
	 */
	public void setStateList(List<SelectItem> stateList) {
		this.stateList = stateList;
	}

	/**
	 * @return the location
	 */
	public List<String> getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(List<String> location) {
		this.location = location;
	}

	/**
	 * @return the selectOption
	 */
	public String getSelectOption() {
		return selectOption;
	}

	/**
	 * @param selectOption
	 *            the selectOption to set
	 */
	public void setSelectOption(String selectOption) {
		this.selectOption = selectOption;
	}

	/**
	 * @return the tenderBean
	 */
	public Date getTenderBean() {
		return tenderBean;
	}

	/**
	 * @param tenderBean
	 *            the tenderBean to set
	 */
	public void setTenderBean(Date tenderBean) {
		this.tenderBean = tenderBean;
	}

	/**
	 * @return the qtno
	 */
	public String getQtno() {
		return qtno;
	}

	/**
	 * @param qtno
	 *            the qtno to set
	 */
	public void setQtno(String qtno) {
		this.qtno = qtno;
	}

	/**
	 * @return the goSodo
	 */
	public List<GoSoDo> getGoSodo() {
		return goSodo;
	}

	/**
	 * @param goSodo
	 *            the goSodo to set
	 */
	public void setGoSodo(List<GoSoDo> goSodo) {
		this.goSodo = goSodo;
	}

	/**
	 * @return the locations
	 */
	public List<Location> getLocations() {
		return locations;
	}

	/**
	 * @param locations
	 *            the locations to set
	 */
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	/**
	 * @return the categories
	 */
	public List<CategoryCode> getCategories() {
		return categories;
	}

	/**
	 * @param categories
	 *            the categories to set
	 */
	public void setCategories(List<CategoryCode> categories) {
		this.categories = categories;
	}

	/**
	 * @return the selectOptions
	 */
	public List<String> getSelectOptions() {
		return selectOptions;
	}

	/**
	 * @param selectOptions
	 *            the selectOptions to set
	 */
	public void setSelectOptions(List<String> selectOptions) {
		this.selectOptions = selectOptions;
	}

	/**
	 * @return the categoryCode
	 */
	public CategoryCode getCategoryCode() {
		return categoryCode;
	}

	/**
	 * @param categoryCode
	 *            the categoryCode to set
	 */
	public void setCategoryCode(CategoryCode categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * @return the mainCcode
	 */
	public List<String> getMainCcode() {
		return mainCcode;
	}

	/**
	 * @param mainCcode
	 *            the mainCcode to set
	 */
	public void setMainCcode(List<String> mainCcode) {
		this.mainCcode = mainCcode;
	}

	/**
	 * @return the subCcode
	 */
	public List<String> getSubCcode() {
		return subCcode;
	}

	/**
	 * @param subCcode
	 *            the subCcode to set
	 */
	public void setSubCcode(List<String> subCcode) {
		this.subCcode = subCcode;
	}

	/**
	 * @return the sucCate
	 */
	public List<String> getSucCate() {
		return sucCate;
	}

	/**
	 * @param sucCate
	 *            the sucCate to set
	 */
	public void setSucCate(List<String> sucCate) {
		this.sucCate = sucCate;
	}

	/**
	 * @return the zones
	 */
	public List<SelectItem> getZones() {
		return zones;
	}

	/**
	 * @param zones
	 *            the zones to set
	 */
	public void setZones(List<SelectItem> zones) {
		this.zones = zones;
	}

	/**
	 * @return the zoneState
	 */
	public List<SelectItem> getZoneState() {
		if (zone != null) {

			String selectedZone = zone.toUpperCase();

			return zoneState.get(selectedZone);

		} else {
			return new ArrayList<SelectItem>();
		}
	}

	/**
	 * @return the zoneCities
	 */
	public List<SelectItem> getZoneCities() {
		if (zoneSt != null) {
			return zoneCities.get(zoneSt);
		} else {
			return new ArrayList<SelectItem>();
		}
	}

	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @param zone
	 *            the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * @return the zoneSt
	 */
	public String getZoneSt() {
		return zoneSt;
	}

	/**
	 * @param zoneSt
	 *            the zoneSt to set
	 */
	public void setZoneSt(String zoneSt) {
		this.zoneSt = zoneSt;
	}

	/**
	 * @return the zoneCt
	 */
	public List<String> getZoneCt() {
		return zoneCt;
	}

	/**
	 * @param zoneCt
	 *            the zoneCt to set
	 */
	public void setZoneCt(List<String> zoneCt) {
		this.zoneCt = zoneCt;
	}

	/**
	 * @param cities
	 *            the cities to set
	 */
	public void setCities(Map<String, List<SelectItem>> cities) {
		this.cities = cities;
	}

	/**
	 * @param zoneState
	 *            the zoneState to set
	 */
	public void setZoneState(Map<String, List<SelectItem>> zoneState) {
		this.zoneState = zoneState;
	}

	/**
	 * @param zoneCities
	 *            the zoneCities to set
	 */
	public void setZoneCities(Map<String, List<SelectItem>> zoneCities) {
		this.zoneCities = zoneCities;
	}

	/**
	 * @return the tenderSelction
	 */
	public String getTenderSelction() {
		return tenderSelction;
	}

	/**
	 * @param tenderSelction
	 *            the tenderSelction to set
	 */
	public void setTenderSelction(String tenderSelction) {
		this.tenderSelction = tenderSelction;
	}

	/**
	 * @return the zoneList
	 */
	public List<Location> getZoneList() {
		return zoneList;
	}

	/**
	 * @param zoneList
	 *            the zoneList to set
	 */
	public void setZoneList(List<Location> zoneList) {
		this.zoneList = zoneList;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the briefDetails
	 */
	public String getBriefDetails() {
		return briefDetails;
	}

	/**
	 * @param briefDetails
	 *            the briefDetails to set
	 */
	public void setBriefDetails(String briefDetails) {
		this.briefDetails = briefDetails;
	}

	/**
	 * @return the visitDetails
	 */
	public String getVisitDetails() {
		return visitDetails;
	}

	/**
	 * @param visitDetails
	 *            the visitDetails to set
	 */
	public void setVisitDetails(String visitDetails) {
		this.visitDetails = visitDetails;
	}

	/**
	 * @return the selectableBean
	 */
	public QtnTenderBean getSelectableBean() {
		return selectableBean;
	}

	/**
	 * @param selectableBean
	 *            the selectableBean to set
	 */
	public void setSelectableBean(QtnTenderBean selectableBean) {
		this.selectableBean = selectableBean;
	}

	/**
	 * @return the dataBean
	 */
	public QtnTenderDataBean getDataBean() {
		return dataBean;
	}

	/**
	 * @param dataBean
	 *            the dataBean to set
	 */
	public void setDataBean(QtnTenderDataBean dataBean) {
		this.dataBean = dataBean;
	}

	/**
	 * @return the supplierList
	 */
	public List<String> getSupplierList() {
		return supplierList;
	}

	/**
	 * @param supplierList
	 *            the supplierList to set
	 */
	public void setSupplierList(List<String> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the scQtApproval
	 */
	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	/**
	 * @param scQtApproval
	 *            the scQtApproval to set
	 */
	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

	/**
	 * @return the approvalTypelist
	 */
	public List<String> getApprovalTypelist() {
		return approvalTypelist;
	}

	/**
	 * @param approvalTypelist
	 *            the approvalTypelist to set
	 */
	public void setApprovalTypelist(List<String> approvalTypelist) {
		this.approvalTypelist = approvalTypelist;
	}

	/**
	 * @return the approvalAction
	 */
	public String getApprovalAction() {
		return approvalAction;
	}

	/**
	 * @param approvalAction
	 *            the approvalAction to set
	 */
	public void setApprovalAction(String approvalAction) {
		this.approvalAction = approvalAction;
	}

	/**
	 * @return the approvalDate
	 */
	public String getApprovalDate() {
		return approvalDate;
	}

	/**
	 * @param approvalDate
	 *            the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return the verifyDate
	 */
	public String getVerifyDate() {
		return verifyDate;
	}

	/**
	 * @param verifyDate
	 *            the verifyDate to set
	 */
	public void setVerifyDate(String verifyDate) {
		this.verifyDate = verifyDate;
	}

	/**
	 * @return the standardDocument
	 */
	public String getStandardDocument() {
		return standardDocument;
	}

	/**
	 * @param standardDocument
	 *            the standardDocument to set
	 */
	public void setStandardDocument(String standardDocument) {
		this.standardDocument = standardDocument;
	}

	/**
	 * @return the verifyAction
	 */
	public String getVerifyAction() {
		return verifyAction;
	}

	/**
	 * @param verifyAction
	 *            the verifyAction to set
	 */
	public void setVerifyAction(String verifyAction) {
		this.verifyAction = verifyAction;
	}

	/**
	 * @return the approverName
	 */
	public String getApproverName() {
		return approverName;
	}

	/**
	 * @param approverName
	 *            the approverName to set
	 */
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	/**
	 * @return the approverstatus
	 */
	public String getApproverstatus() {
		return approverstatus;
	}

	/**
	 * @param approverstatus
	 *            the approverstatus to set
	 */
	public void setApproverstatus(String approverstatus) {
		this.approverstatus = approverstatus;
	}

	/**
	 * @return the verifierName
	 */
	public String getVerifierName() {
		return verifierName;
	}

	/**
	 * @param verifierName
	 *            the verifierName to set
	 */
	public void setVerifierName(String verifierName) {
		this.verifierName = verifierName;
	}

	/**
	 * @return the verifierStatus
	 */
	public String getVerifierStatus() {
		return verifierStatus;
	}

	/**
	 * @param verifierStatus
	 *            the verifierStatus to set
	 */
	public void setVerifierStatus(String verifierStatus) {
		this.verifierStatus = verifierStatus;
	}

	/**
	 * @return the scAttachment
	 */
	public ScAttachment getScAttachment() {
		return scAttachment;
	}

	/**
	 * @param scAttachment
	 *            the scAttachment to set
	 */
	public void setScAttachment(ScAttachment scAttachment) {
		this.scAttachment = scAttachment;
	}

	/**
	 * @return the tabList
	 */
	public List<TabBean> getTabList() {
		return tabList;
	}

	/**
	 * @param tabList
	 *            the tabList to set
	 */
	public void setTabList(List<TabBean> tabList) {
		this.tabList = tabList;
	}

	/**
	 * @return the sampleList
	 */
	public List<ScQtApproval> getSampleList() {
		return sampleList;
	}

	/**
	 * @param sampleList the sampleList to set
	 */
	public void setSampleList(List<ScQtApproval> sampleList) {
		this.sampleList = sampleList;
	}

}
