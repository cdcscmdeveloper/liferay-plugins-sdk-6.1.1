/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;

import org.primefaces.model.TreeNode;

/**
 * The Class FLCtSupplierItemVO.
 * 
 * @author Giribabu
 * @since 22/9/2014
 */
public class FLCtSupplierItemVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private CtItemPrice  supplierItemPrice;
	private TreeNode itemZoneTree;
	
	/**
	 * @return the supplierItemPrice
	 */
	public CtItemPrice getSupplierItemPrice() {
		return supplierItemPrice;
	}
	/**
	 * @param supplierItemPrice the supplierItemPrice to set
	 */
	public void setSupplierItemPrice(CtItemPrice supplierItemPrice) {
		this.supplierItemPrice = supplierItemPrice;
	}
	/**
	 * @return the itemZoneTree
	 */
	public TreeNode getItemZoneTree() {
		return itemZoneTree;
	}
	/**
	 * @param itemZoneTree the itemZoneTree to set
	 */
	public void setItemZoneTree(TreeNode itemZoneTree) {
		this.itemZoneTree = itemZoneTree;
	}
	
	
}
