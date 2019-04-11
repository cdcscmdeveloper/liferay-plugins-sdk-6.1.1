/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.race;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Race
 * 
 * @author Ken
 * @since 24.10.2016
 */
@ManagedBean(name = "raceManagedBean")
@SessionScoped
public class RaceManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private PmRace pmRace;
	private String raceCode;
	private String raceNames;
	private Long recordStatus;
	private boolean isLazy;
	
	public RaceManagedBean(){}

	/**
	 * @return the pmRace
	 */
	public PmRace getPmRace() {
		return pmRace;
	}

	/**
	 * @param pmRace the pmRace to set
	 */
	public void setPmRace(PmRace pmRace) {
		this.pmRace = pmRace;
	}

	/**
	 * @return the raceCode
	 */
	public String getRaceCode() {
		return raceCode;
	}

	/**
	 * @param raceCode the raceCode to set
	 */
	public void setRaceCode(String raceCode) {
		this.raceCode = raceCode;
	}

	/**
	 * @return the raceNames
	 */
	public String getRaceNames() {
		return raceNames;
	}

	/**
	 * @param raceNames the raceNames to set
	 */
	public void setRaceNames(String raceNames) {
		this.raceNames = raceNames;
	}

	/**
	 * @return the recordStatus
	 */
	public Long getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus the recordStatus to set
	 */
	public void setRecordStatus(Long recordStatus) {
		this.recordStatus = recordStatus;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	
}
