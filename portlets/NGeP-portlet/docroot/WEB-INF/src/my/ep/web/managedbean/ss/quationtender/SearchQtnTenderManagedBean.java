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

import my.ep.web.bean.ss.quatationtender.GoSoDo;
import my.ep.web.bean.ss.quatationtender.Location;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "searchQtnTenderMB")
@SessionScoped
public class SearchQtnTenderManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	
		private String selectOption;
	private static final long serialVersionUID = 1L;

	private List<QtnTenderBean> qtnTenders;
	
	private List<GoSoDo> goSodo;
	
	private List<Location> locations = new ArrayList();

	private String qtno;
	private Date tenderBean;
	
	private boolean checked;

	private List<SelectItem> stateList;

	private Map<String, List<SelectItem>> cities;

	private List<String> location;

	private String state;

	public SearchQtnTenderManagedBean() {
		qtnTenders = populateSearchQtnBean();
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

	}
	
	public String addAction() {
		 
		Location order = new Location(state,null);
 
		locations.add(order);
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

	/**
	 * @param cities
	 *            the cities to set
	 */
	public void setCities(Map<String, List<SelectItem>> cities) {
		this.cities = cities;
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

}
