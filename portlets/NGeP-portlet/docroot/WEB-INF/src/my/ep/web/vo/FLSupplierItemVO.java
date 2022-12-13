/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.io.Serializable;

import org.primefaces.model.TreeNode;

import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;

/**
 * The Class FLSupplierItemVO.
 * 
 * @author Giribabu
 * @since 22/9/2014
 */
public class FLSupplierItemVO implements Serializable{

	private static final long serialVersionUID = 1L;
	private ScSupplierItemPrice  supplierItemPrice;
	private TreeNode itemZoneTree;
	/**
	 * @return the supplierItemPrice
	 */
	public ScSupplierItemPrice getSupplierItemPrice() {
		return supplierItemPrice;
	}
	/**
	 * @return the itemZoneTree
	 */
	public TreeNode getItemZoneTree() {
		return itemZoneTree;
	}
	/**
	 * @param supplierItemPrice the supplierItemPrice to set
	 */
	public void setSupplierItemPrice(ScSupplierItemPrice supplierItemPrice) {
		this.supplierItemPrice = supplierItemPrice;
	}
	/**
	 * @param itemZoneTree the itemZoneTree to set
	 */
	public void setItemZoneTree(TreeNode itemZoneTree) {
		this.itemZoneTree = itemZoneTree;
	}
	

}
