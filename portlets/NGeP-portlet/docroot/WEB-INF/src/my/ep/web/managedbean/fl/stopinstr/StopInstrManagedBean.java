/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.stopinstr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;

/**
 * ManagedBean for Stop Instruction
 * 
 * @author Kieyun
 * @since 7/7/2014
 */
@ManagedBean
@SessionScoped
public class StopInstrManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private int selectedTabIndex;
	
	private FlStopInstr stopInstr;
	private TreeNode chargeLineItemRoot ;
	
	private List<PmReasonDesc> reasonDescs;
	
	public List<PmReasonDesc> getReasonDescs() {
		if(reasonDescs==null) {
			reasonDescs = new ArrayList<PmReasonDesc>();
		}
		return reasonDescs;
	}

	public void setReasonDescs(List<PmReasonDesc> reasonDescs) {
		this.reasonDescs = reasonDescs;
	}

	public FlStopInstr getStopInstr() {
		return stopInstr;
	}

	public void setStopInstr(FlStopInstr stopInstr) {
		this.stopInstr = stopInstr;
	}

	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}

	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
}
