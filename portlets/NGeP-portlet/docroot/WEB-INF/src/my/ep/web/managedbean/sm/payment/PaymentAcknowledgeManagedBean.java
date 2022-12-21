package my.ep.web.managedbean.sm.payment;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "paymentAcknowledgeMB")
@SessionScoped
public class PaymentAcknowledgeManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private Long paymentId;
	
	public Long getPaymentId() {
		return paymentId;
	}
	
	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}
}
