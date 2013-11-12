/**
 * 
 */
package my.ep.web.managedbean.fl.invoice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for invoice
 * @author KK LIM
 * @since 30/10/2013
 *
 */
@ManagedBean(name = "invMB")
@SessionScoped
public class InvoiceManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FlInvoice invoice;
	
	private String bpmTaskId;
	private String[] actions;
	private String DocProcess;
	private String buttonVal;
	
	
	/**
	 * @return the invoice
	 */
	public FlInvoice getInvoice() {
		return invoice;
	}
	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(FlInvoice invoice) {
		this.invoice = invoice;
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
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return DocProcess;
	}
	/**
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		DocProcess = docProcess;
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
}
