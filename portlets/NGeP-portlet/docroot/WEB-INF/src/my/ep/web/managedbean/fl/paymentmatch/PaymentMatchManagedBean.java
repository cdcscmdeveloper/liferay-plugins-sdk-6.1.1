/**
 * 
 */
package my.ep.web.managedbean.fl.paymentmatch;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author admin
 *
 */
@ManagedBean
@SessionScoped
public class PaymentMatchManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FlPaymentAdvice paymentAdvice;
	
	//getbpm
		private String bpmTaskId;
		private String[] actions;


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
	 * @return the paymentAdvice
	 */
	public FlPaymentAdvice getPaymentAdvice() {
		return paymentAdvice;
	}

	/**
	 * @param paymentAdvice the paymentAdvice to set
	 */
	public void setPaymentAdvice(FlPaymentAdvice paymentAdvice) {
		this.paymentAdvice = paymentAdvice;
	}
}
