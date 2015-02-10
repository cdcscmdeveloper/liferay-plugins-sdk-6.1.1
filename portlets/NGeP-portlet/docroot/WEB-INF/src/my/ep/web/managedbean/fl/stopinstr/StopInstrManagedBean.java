/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.stopinstr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.pm.common.PmDigiSign;
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
	
	private String buttonVal;
	
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate;
	private String textBeforeSign;
	private String buttonAction;
	private String backToDocument;
	
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

	/**
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}

	/**
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}

	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public String getButtonAction() {
		return buttonAction;
	}

	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * @return the backToDocument
	 */
	public String getBackToDocument() {
		return backToDocument;
	}

	/**
	 * @param backToDocument the backToDocument to set
	 */
	public void setBackToDocument(String backToDocument) {
		this.backToDocument = backToDocument;
	}
}
