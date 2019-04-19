/*
 * Copyright © 2017 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.pojobweight;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmPoJobWeight;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Po Job Weight
 * 
 * @author Ken
 * @since 13/03/2017
 */
@ManagedBean(name = "poJobWeightManagedBean")
@SessionScoped
public class PoJobWeightManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private List<PmPoJobWeight> jobWeightList;
	private List<PmPoJobWeight> oriJobWeightList;

	public PoJobWeightManagedBean (){}

	
	/**
	 * @return the jobWeightList
	 */
	public List<PmPoJobWeight> getJobWeightList() {
		return jobWeightList;
	}

	/**
	 * @param jobWeightList the jobWeightList to set
	 */
	public void setJobWeightList(List<PmPoJobWeight> jobWeightList) {
		this.jobWeightList = jobWeightList;
	}


	/**
	 * @return the oriJobWeightList
	 */
	public List<PmPoJobWeight> getOriJobWeightList() {
		if(oriJobWeightList == null){
			oriJobWeightList = new ArrayList<PmPoJobWeight>();
		}
		return oriJobWeightList;
	}


	/**
	 * @param oriJobWeightList the oriJobWeightList to set
	 */
	public void setOriJobWeightList(List<PmPoJobWeight> oriJobWeightList) {
		this.oriJobWeightList = oriJobWeightList;
	}


}
