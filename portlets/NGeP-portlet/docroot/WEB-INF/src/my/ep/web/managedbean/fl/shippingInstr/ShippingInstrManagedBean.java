/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.shippingInstr;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Shipping Instruction
 * 
 * @author KK LIM
 * @since 28/10/2013
 *
 */
@ManagedBean(name="shippingInstrMB")
@SessionScoped
public class ShippingInstrManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FlShippingInstr shippingInstr;
	
	//getbpm
		private String bpmTaskId;
		private String[] actions;
		
		//getcontract
		private CtContract ctContract = new CtContract();
		private List <CtContract> contractList;
		private CtContract selectedContract;
		private String tempLogin;
		private FlFulfilmentRequest flRequest;
		

	/**
	 * @return the shippingInstr
	 */
	public FlShippingInstr getShippingInstr() {
		return shippingInstr;
	}

	/**
	 * @param shippingInstr the shippingInstr to set
	 */
	public void setShippingInstr(FlShippingInstr shippingInstr) {
		this.shippingInstr = shippingInstr;
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

	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}

	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	/**
	 * @return the contractList
	 */
	public List<CtContract> getContractList() {
		return contractList;
	}

	/**
	 * @param contractList the contractList to set
	 */
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}

	/**
	 * @return the selectedContract
	 */
	public CtContract getSelectedContract() {
		return selectedContract;
	}

	/**
	 * @param selectedContract the selectedContract to set
	 */
	public void setSelectedContract(CtContract selectedContract) {
		this.selectedContract = selectedContract;
	}

	/**
	 * @return the tempLogin
	 */
	public String getTempLogin() {
		return tempLogin;
	}

	/**
	 * @param tempLogin the tempLogin to set
	 */
	public void setTempLogin(String tempLogin) {
		this.tempLogin = tempLogin;
	}

	/**
	 * @return the flRequest
	 */
	public FlFulfilmentRequest getFlRequest() {
		return flRequest;
	}

	/**
	 * @param flRequest the flRequest to set
	 */
	public void setFlRequest(FlFulfilmentRequest flRequest) {
		this.flRequest = flRequest;
	}


}
