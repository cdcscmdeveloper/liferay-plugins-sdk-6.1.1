/**
 * Copyright © 2013 Archisoft. All rights reserved.

 */
package my.ep.web.managedbean.fl.dan;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * Managed Bean for Debit Adjustment Note
 * @author KKLIM
 * @since 1/11/2013
 */
@ManagedBean(name = "danMB")
@SessionScoped
public class DebitAdjustmentNoteManagedBean extends BaseManagedBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteManagedBean.class);
	
	private FlAdjustment adjustment;
	private TreeNode danChargeLineTree;
	private TreeNode[] selectedNodes;
	
	private boolean isDan = false;
	/**
	 * @return the adjustment
	 */
	public FlAdjustment getAdjustment() {
		return adjustment;
	}
	/**
	 * @param adjustment the adjustment to set
	 */
	public void setAdjustment(FlAdjustment adjustment) {
		this.adjustment = adjustment;
	}
	/**
	 * @return the isDan
	 */
	public boolean isDan() {
		return isDan;
	}
	/**
	 * @param isDan the isDan to set
	 */
	public void setDan(boolean isDan) {
		this.isDan = isDan;
	}
	/**
	 * @return the danChargeLineTree
	 */
	public TreeNode getDanChargeLineTree() {
		return danChargeLineTree;
	}
	/**
	 * @param danChargeLineTree the danChargeLineTree to set
	 */
	public void setDanChargeLineTree(TreeNode danChargeLineTree) {
		this.danChargeLineTree = danChargeLineTree;
	}
	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}
	/**
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
	
}
