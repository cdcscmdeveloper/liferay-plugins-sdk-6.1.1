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

import my.ep.web.bean.ss.quatationtender.CategoryCode;
import my.ep.web.bean.ss.quatationtender.GoSoDo;
import my.ep.web.bean.ss.quatationtender.Location;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
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

	public Map<String, TabBean> getTabview() {
		return tabview;
	}

	public void setTabview(Map<String, TabBean> tabview) {
		this.tabview = tabview;
	}


		
		private String tenderSelction;

	private static final long serialVersionUID = 1L;

	private List<QtnTenderBean> qtnTenders;
	
	private List<GoSoDo> goSodo;
	
	private List<Location> locations;
	private List<Location> zoneList;

	private String qtno;
	private Date tenderBean= new Date();
	
	private Date date;
	
	private boolean checked;

	private List<SelectItem> stateList;

	private Map<String, List<SelectItem>> cities;

	private List<String> location;

	private String state;

	 private boolean selectedBoolean;
	 
	 private String  selectedBoolean1;
	 
	  private List<String> selectedOptions; 
	  
	  private Map<String, TabBean> tabview;
	  
	  private List<TabBean> tabBean;
	  
	  private List<SelectItem> checkBoxList;
	  private List<String> checkBoxBPK;


	/*public Map<String, List<SelectItem>> getTabview() {
		return tabview;
	}

	public void setTabview(Map<String, List<SelectItem>> tabview) {
		this.tabview = tabview;
	}*/

	


	public List<SelectItem> getCheckBoxList() {
		return checkBoxList;
	}

	public List<String> getCheckBoxBPK() {
		return checkBoxBPK;
	}

	public void setCheckBoxBPK(List<String> checkBoxBPK) {
		this.checkBoxBPK = checkBoxBPK;
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
	
	private Map<String,List<SelectItem>> zoneCities;


	public SearchQtnTenderManagedBean() {
		locations = new ArrayList<Location>();
		zoneList= new ArrayList<Location>();
		qtnTenders = populateSearchQtnBean();
		
		categories= populateCategory();
		
		mainCcode= populateMainCodes();
		subCcode= populateSubCodes();
		sucCate= populateSucCate();
	goSodo=populateGosodo();
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

        tabview=new HashMap<>();
		tabview.put("BudgetApproval", new TabBean("Budget Approval"));
		tabview.put("BPKApproval", new TabBean("BPKApproval"));
		tabview.put("PMSabah", new TabBean("PMSabah"));
		tabview.put("PMSarawak", new TabBean("PMSarawak"));
		tabview.put("MITIApproval", new TabBean("MITIApproval"));
		tabview.put("ICTApproval", new TabBean("ICTApproval"));
		tabview.put("PBApproval", new TabBean("PBApproval"));
		tabview.put("PTJApproval", new TabBean("PTJApproval"));
		checkBoxList=new ArrayList();
		checkBoxList.add(new SelectItem("BudgetApproval","BudgetApproval"));
		checkBoxList.add(new SelectItem("BPKApproval","BPKApproval"));
		checkBoxList.add(new SelectItem("PMSabah","PMSabah"));
		checkBoxList.add(new SelectItem("PMSarawak","PMSarawak"));
		checkBoxList.add(new SelectItem("MITIApproval","MITIApproval"));
		checkBoxList.add(new SelectItem("ICTApproval","ICTApproval"));
		checkBoxList.add(new SelectItem("PBApproval","ProcurementBoardApproval"));
		checkBoxList.add(new SelectItem("PTJApproval","PTJApproval"));
		

		
		zones= new ArrayList<SelectItem>();
		zoneState=new HashMap<String,List<SelectItem>>();
		zoneCities= new HashMap<String,List<SelectItem>>();
		zones.add(new SelectItem("South"));
		zones.add(new SelectItem("North"));
		zones.add(new SelectItem("East"));

		List<SelectItem> mstates= new ArrayList<SelectItem>();
		mstates.add(new SelectItem("Maine"));
		mstates.add(new SelectItem("South Carolina"));
		mstates.add(new SelectItem("Illinois"));
		zoneState.put("South", mstates);
		List<SelectItem> istates= new ArrayList<SelectItem>();
		istates.add(new SelectItem("Maine"));
		istates.add(new SelectItem("South Carolina"));
		istates.add(new SelectItem("Illinois"));
		zoneState.put("North", istates);
		List<SelectItem> cstates= new ArrayList<SelectItem>();
		cstates.add(new SelectItem("Maine"));
		cstates.add(new SelectItem("South Carolina"));
		cstates.add(new SelectItem("Illinois"));
		zoneState.put("East", cstates);
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
	 List<String> mainCodes= new ArrayList();
	 mainCodes.add("11");
	 mainCodes.add("22");
	 
		return mainCodes;
	}

	private List<CategoryCode> populateCategory() {
		List<CategoryCode> ccodes= new ArrayList();
		CategoryCode categoryCode= new CategoryCode();
		categoryCode.setNum(1);
		categoryCode.setMainCCode("11");
		categoryCode.setSubCCode("22");
		categoryCode.setSubCDevison("33");
		categoryCode.setManditory(true);
		ccodes.add(categoryCode);
		CategoryCode _categoryCode= new CategoryCode();
		_categoryCode.setNum(2);
		_categoryCode.setMainCCode("44");
		_categoryCode.setSubCCode("55");
		_categoryCode.setSubCDevison("66");
		_categoryCode.setManditory(true);
		ccodes.add(_categoryCode);
		return ccodes;
	}

	public String addAction() {
		 
		Location order = new Location(state,location);
 
		locations.add(order);
		return null;
	}
	
	public String addZoneAction() {
		 
		Location order = new Location(zone,zoneSt,zoneCt);
 
		zoneList.add(order);
		return null;
	}


	private List<GoSoDo> populateGosodo() {
		List<GoSoDo> gsdList= new ArrayList<GoSoDo>();
		GoSoDo goSoDo= new GoSoDo();
		goSoDo.setBdgAvail("1000");
		goSoDo.setType("Test");
		gsdList.add(goSoDo);
		GoSoDo goSoDo1= new GoSoDo();
		goSoDo1.setBdgAvail("2000");
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
	 * @param selectOption the selectOption to set
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
	 * @param tenderBean the tenderBean to set
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
	 * @param qtno the qtno to set
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
	 * @param goSodo the goSodo to set
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
	 * @param locations the locations to set
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
	 * @param categories the categories to set
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
	 * @param selectOptions the selectOptions to set
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
	 * @param categoryCode the categoryCode to set
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
	 * @param mainCcode the mainCcode to set
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
	 * @param subCcode the subCcode to set
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
	 * @param sucCate the sucCate to set
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
	 * @param zones the zones to set
	 */
	public void setZones(List<SelectItem> zones) {
		this.zones = zones;
	}

	/**
	 * @return the zoneState
	 */
	public  List<SelectItem> getZoneState() {
		if(zone != null){
			return zoneState.get(zone);
		}else{
				return new ArrayList<SelectItem>();
		}
	}

	
	/**
	 * @return the zoneCities
	 */
	public List<SelectItem> getZoneCities() {
		if(zoneSt !=null){
			return zoneCities.get(zoneSt);
		}else{
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
	 * @param zone the zone to set
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
	 * @param zoneSt the zoneSt to set
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
	 * @param zoneCt the zoneCt to set
	 */
	public void setZoneCt(List<String> zoneCt) {
		this.zoneCt = zoneCt;
	}

	/**
	 * @param cities the cities to set
	 */
	public void setCities(Map<String, List<SelectItem>> cities) {
		this.cities = cities;
	}

	/**
	 * @param zoneState the zoneState to set
	 */
	public void setZoneState(Map<String, List<SelectItem>> zoneState) {
		this.zoneState = zoneState;
	}

	/**
	 * @param zoneCities the zoneCities to set
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
	 * @param tenderSelction the tenderSelction to set
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
	 * @param zoneList the zoneList to set
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
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	
	
}
