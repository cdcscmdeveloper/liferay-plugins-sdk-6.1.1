/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.pm.zone;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.common.PmZoneState;
import my.ep.web.bean.pm.ZoneSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Zone
 * 
 * @author venugopal
 * @since 30/7/2014
 */
@ManagedBean(name = "zoneManagedBean")
@SessionScoped
public class ZoneManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The zone search bean. */
	private ZoneSearchBean zoneSearchBean;

	/** The pm zone. */
	private PmZone pmZone;

	/** The pm zone. */
	private boolean isLazy = false;

	/** The states. */
	private List<PmState> states = new ArrayList<PmState>();

	/** The selected states. */
	private List<PmState> selectedStates = new ArrayList<PmState>();

	/** The pm state. */
	private PmState pmState;

	/** The zone state list. */
	private List<PmZoneState> zoneStateList = new ArrayList<PmZoneState>();

	/** The selected zones. */
	private List<PmZoneState> selectedZones = new ArrayList<PmZoneState>();

	/** The selected zone. */
	private PmZoneState selectedZone;

	/** The selections. */
	private List<SelectItem> selections;

	/** The show parent msg. */
	private boolean showParentMsg;
	
	/** The state list. */
	private List<PmState> stateList;

	/** The select state. */
	private PmState selectState;

	/** The pm zones list. */
	private List<PmZone> pmZonesList;

	/************ AUDIT #28895_35509 *************/
	/** The pm zone OLD Value. */
	private PmZone pmZoneOld;

	/** The pm zone. */
	private PmZone resZone;

	/** ZoneResDTO */
	private ZoneResDTO zoneResDTO;

	/** The zone state list. */
	private List<PmZoneState> zoneStateListDeleted = new ArrayList<PmZoneState>();

	/** The zone state list. */
	private List<PmZoneState> zoneStateListNewAdded = new ArrayList<PmZoneState>();

	/************ END AUDIT #28895_35509 END *************/
	
	/**
	 * Instantiates a new zone managed bean.
	 */
	public ZoneManagedBean() {
		pmZone = new PmZone();
	}

	/**
	 * Gets the pm zone.
	 * 
	 * @return the pmZone
	 */
	public PmZone getPmZone() {
		return pmZone;
	}

	/**
	 * Sets the pm zone.
	 * 
	 * @param pmZone
	 *            the pmZone to set
	 */
	public void setPmZone(PmZone pmZone) {
		this.pmZone = pmZone;
	}

	/**
	 * Gets the state list.
	 * 
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * Sets the state list.
	 * 
	 * @param stateList
	 *            the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * Gets the select state.
	 * 
	 * @return the selectState
	 */
	public PmState getSelectState() {
		return selectState;
	}

	/**
	 * Sets the select state.
	 * 
	 * @param selectState
	 *            the selectState to set
	 */
	public void setSelectState(PmState selectState) {
		this.selectState = selectState;
	}

	/**
	 * Gets the states.
	 * 
	 * @return the states
	 */
	public List<PmState> getStates() {
		return states;
	}

	/**
	 * Sets the states.
	 * 
	 * @param states
	 *            the states to set
	 */
	public void setStates(List<PmState> states) {
		this.states = states;
	}

	/**
	 * Gets the selected states.
	 * 
	 * @return the selectedStates
	 */
	public List<PmState> getSelectedStates() {
		return selectedStates;
	}

	/**
	 * Sets the selected states.
	 * 
	 * @param selectedStates
	 *            the selectedStates to set
	 */
	public void setSelectedStates(List<PmState> selectedStates) {
		this.selectedStates = selectedStates;
	}

	/**
	 * Gets the selections.
	 * 
	 * @return the selections
	 */
	public List<SelectItem> getSelections() {
		return selections;
	}

	/**
	 * Sets the selections.
	 * 
	 * @param selections
	 *            the selections to set
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	/**
	 * Gets the selected zone.
	 * 
	 * @return the selectedZone
	 */
	public PmZoneState getSelectedZone() {
		return selectedZone;
	}

	/**
	 * Sets the selected zone.
	 * 
	 * @param selectedZone
	 *            the selectedZone to set
	 */
	public void setSelectedZone(PmZoneState selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * Gets the zone state list.
	 * 
	 * @return the zoneStateList
	 */
	public List<PmZoneState> getZoneStateList() {
		return zoneStateList;
	}

	/**
	 * Sets the zone state list.
	 * 
	 * @param zoneStateList
	 *            the zoneStateList to set
	 */
	public void setZoneStateList(List<PmZoneState> zoneStateList) {
		this.zoneStateList = zoneStateList;
	}

	/**
	 * Gets the pm state.
	 * 
	 * @return the pmState
	 */
	public PmState getPmState() {
		return pmState;
	}

	/**
	 * Sets the pm state.
	 * 
	 * @param pmState
	 *            the pmState to set
	 */
	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}

	/**
	 * Gets the selected zones.
	 * 
	 * @return the selectedZones
	 */
	public List<PmZoneState> getSelectedZones() {
		return selectedZones;
	}

	/**
	 * Sets the selected zones.
	 * 
	 * @param selectedZones
	 *            the selectedZones to set
	 */
	public void setSelectedZones(List<PmZoneState> selectedZones) {
		this.selectedZones = selectedZones;
	}

	/**
	 * Gets the zone search bean.
	 * 
	 * @return the zoneSearchBean
	 */
	public ZoneSearchBean getZoneSearchBean() {
		if (zoneSearchBean == null)
			zoneSearchBean = new ZoneSearchBean();
		return zoneSearchBean;
	}

	/**
	 * Sets the zone search bean.
	 * 
	 * @param zoneSearchBean
	 *            the zoneSearchBean to set
	 */
	public void setZoneSearchBean(ZoneSearchBean zoneSearchBean) {
		this.zoneSearchBean = zoneSearchBean;
	}

	/**
	 * Gets the pm zones list.
	 * 
	 * @return the pmZonesList
	 */
	public List<PmZone> getPmZonesList() {
		return pmZonesList;
	}

	/**
	 * Sets the pm zones list.
	 * 
	 * @param pmZonesList
	 *            the pmZonesList to set
	 */
	public void setPmZonesList(List<PmZone> pmZonesList) {
		this.pmZonesList = pmZonesList;
	}

	/**
	 * Checks if is lazy.
	 * 
	 * @return true, if is lazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * Sets the lazy.
	 * 
	 * @param isLazy
	 *            the new lazy
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
	/**
	 * Checks if is show parent msg.
	 * 
	 * @return the showParentMsg
	 */
	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	/**
	 * Sets the show parent msg.
	 * 
	 * @param showParentMsg
	 *            the showParentMsg to set
	 */
	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	//START: #35509
	public PmZone getPmZoneOld() {
		return pmZoneOld;
	}

	public void setPmZoneOld(PmZone pmZoneOld) {
		this.pmZoneOld = pmZoneOld;
	}

	public PmZone getResZone() {
		return resZone;
	}

	public void setResZone(PmZone resZone) {
		this.resZone = resZone;
	}

	public ZoneResDTO getZoneResDTO() {
		return zoneResDTO;
	}

	public void setZoneResDTO(ZoneResDTO zoneResDTO) {
		this.zoneResDTO = zoneResDTO;
	}

	public List<PmZoneState> getZoneStateListDeleted() {
		return zoneStateListDeleted;
	}

	public void setZoneStateListDeleted(List<PmZoneState> zoneStateListDeleted) {
		this.zoneStateListDeleted = zoneStateListDeleted;
	}

	public List<PmZoneState> getZoneStateListNewAdded() {
		return zoneStateListNewAdded;
	}

	public void setZoneStateListNewAdded(List<PmZoneState> zoneStateListNewAdded) {
		this.zoneStateListNewAdded = zoneStateListNewAdded;
	}
	//END: #35509

}
