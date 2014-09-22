/**
 * 
 */
package my.ep.web.backingbean.fl.pr;

import java.io.Serializable;

import org.primefaces.model.TreeNode;

import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;

/**
 * @author Ramana
 *
 */
public class FLSupplierItemVO implements Serializable{

	/**
	 * 
	 */
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
