/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.tpl.spec;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;

/**
 * Zone Location Managed Bean
 * 
 * @author Kenny
 * @since 31.05.2014
 */

@ManagedBean(name="zoneLocMB")
@SessionScoped
public class ZoneLocationManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	 
	private TplSpecZoneDtl tplSpecZoneDtl;
	private TreeNode locationTree;
	
	public ZoneLocationManagedBean() {
	}

	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * @return the locationTree
	 */
	public TreeNode getLocationTree() {
		return locationTree;
	}

	/**
	 * @param locationTree the locationTree to set
	 */
	public void setLocationTree(TreeNode locationTree) {
		this.locationTree = locationTree;
	}

}
