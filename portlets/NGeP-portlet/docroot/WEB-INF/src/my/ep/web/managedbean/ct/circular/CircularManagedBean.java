/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

/**
 * Circular Managed Bean
 * 
 * @author kieyun
 * @Created 23/12/2014
 */
@ManagedBean
@SessionScoped
public class CircularManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String searchCircularNo;
	private String searchCircularName;
	private String searchPhysicalCircularNo;
	
	public String getSearchCircularNo() {
		return searchCircularNo;
	}
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}
	public String getSearchCircularName() {
		return searchCircularName;
	}
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}
	public String getSearchPhysicalCircularNo() {
		return searchPhysicalCircularNo;
	}
	public void setSearchPhysicalCircularNo(String searchPhysicalCircularNo) {
		this.searchPhysicalCircularNo = searchPhysicalCircularNo;
	}
	
	
}
