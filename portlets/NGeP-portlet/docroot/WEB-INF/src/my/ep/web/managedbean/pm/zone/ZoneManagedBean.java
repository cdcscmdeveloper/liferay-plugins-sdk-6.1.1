package my.ep.web.managedbean.pm.zone;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.common.PmZoneState;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "zoneManagedBean")
@SessionScoped
public class ZoneManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String zoneCode;
	private String zoneName;
	private String stateName;
	private String status;
	private String stateCode;
	private PmZone pmZone;
	private List<PmState> states = new ArrayList<PmState>();
	private List<PmState> selectedStates = new ArrayList<PmState>();
	private PmState pmState;
	private List<PmZoneState> zoneStateList= new ArrayList<PmZoneState>();
	private List<PmZoneState> selectedZones = new ArrayList<PmZoneState>();
	private PmZoneState selectedZone;
	private List<SelectItem> selections;

	private List<PmState> stateList;
	private PmState selectState;

	public ZoneManagedBean() {
		pmZone = new PmZone();
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	

	/**
	 * @return the pmZone
	 */
	public PmZone getPmZone() {
		return pmZone;
	}

	/**
	 * @param pmZone
	 *            the pmZone to set
	 */
	public void setPmZone(PmZone pmZone) {
		this.pmZone = pmZone;
	}

	/**
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * @param stateList
	 *            the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * @return the selectState
	 */
	public PmState getSelectState() {
		return selectState;
	}

	/**
	 * @param selectState
	 *            the selectState to set
	 */
	public void setSelectState(PmState selectState) {
		this.selectState = selectState;
	}

	/**
	 * @return the states
	 */
	public List<PmState> getStates() {
		return states;
	}

	/**
	 * @param states
	 *            the states to set
	 */
	public void setStates(List<PmState> states) {
		this.states = states;
	}

	/**
	 * @return the selectedStates
	 */
	public List<PmState> getSelectedStates() {
		return selectedStates;
	}

	/**
	 * @param selectedStates
	 *            the selectedStates to set
	 */
	public void setSelectedStates(List<PmState> selectedStates) {
		this.selectedStates = selectedStates;
	}

	/**
	 * @return the selections
	 */
	public List<SelectItem> getSelections() {
		return selections;
	}

	/**
	 * @param selections
	 *            the selections to set
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	

	/**
	 * @return the selectedZone
	 */
	public PmZoneState getSelectedZone() {
		return selectedZone;
	}

	/**
	 * @param selectedZone the selectedZone to set
	 */
	public void setSelectedZone(PmZoneState selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * @return the zoneStateList
	 */
	public List<PmZoneState> getZoneStateList() {
		return zoneStateList;
	}

	/**
	 * @param zoneStateList the zoneStateList to set
	 */
	public void setZoneStateList(List<PmZoneState> zoneStateList) {
		this.zoneStateList = zoneStateList;
	}

	/**
	 * @return the pmState
	 */
	public PmState getPmState() {
		return pmState;
	}

	/**
	 * @param pmState the pmState to set
	 */
	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}

	/**
	 * @return the selectedZones
	 */
	public List<PmZoneState> getSelectedZones() {
		return selectedZones;
	}

	/**
	 * @param selectedZones the selectedZones to set
	 */
	public void setSelectedZones(List<PmZoneState> selectedZones) {
		this.selectedZones = selectedZones;
	}

	

}
