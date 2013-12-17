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

	// for Deduction panel display	
	private String docType;
	

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
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
