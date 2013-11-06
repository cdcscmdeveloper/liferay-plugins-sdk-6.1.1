/**
 * Copyright © 2013 Archisoft. All rights reserved.

 */
package my.ep.web.managedbean.fl.dan;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Debit Adjustment Note
 * @author KKLIM
 * @since 1/11/2013
 */
@ManagedBean
@SessionScoped
public class DebitAdjustmentNoteManagedBean extends BaseManagedBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteManagedBean.class);
	
	private FlAdjustment adjustment;
	
	private String bpmTaskId;
	private String[] actions;
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
	 * @return the bpmTaskId
	 */
	public String getBpmTaskId() {
		return bpmTaskId;
	}
	/**
	 * @param bpmTaskId the bpmTaskId to set
	 */
	public void setBpmTaskId(String bpmTaskId) {
		this.bpmTaskId = bpmTaskId;
	}
	/**
	 * @return the actions
	 */
	public String[] getActions() {
		return actions;
	}
	/**
	 * @param actions the actions to set
	 */
	public void setActions(String[] actions) {
		this.actions = actions;
	}
	
}
