/**
 * 
 */
package my.ep.web.managedbean.fl.invoice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
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
	
	private FlInvoice invoice = new FlInvoice();
	private FlInvoiceItem invItem = new FlInvoiceItem();
	
	private String DocProcess;
	private String buttonVal;
	
	private boolean isDan = false;
	private boolean isUpdateDan = false;
	
	private String factoringCheck="false";
	
	
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
	 * @return the isUpdateDan
	 */
	public boolean isUpdateDan() {
		return isUpdateDan;
	}
	/**
	 * @param isUpdateDan the isUpdateDan to set
	 */
	public void setUpdateDan(boolean isUpdateDan) {
		this.isUpdateDan = isUpdateDan;
	}
	/**
	 * @return the factoringCheck
	 */
	public String getFactoringCheck() {
		return factoringCheck;
	}
	/**
	 * @param factoringCheck the factoringCheck to set
	 */
	public void setFactoringCheck(String factoringCheck) {
		this.factoringCheck = factoringCheck;
	}
	/**
	 * @return the invItem
	 */
	public FlInvoiceItem getInvItem() {
		return invItem;
	}
	/**
	 * @param invItem the invItem to set
	 */
	public void setInvItem(FlInvoiceItem invItem) {
		this.invItem = invItem;
	}
}
