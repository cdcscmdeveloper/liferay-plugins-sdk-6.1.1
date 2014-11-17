package my.ep.web.managedbean.sm.payment;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "paymentAcknowlegdeMB")
@SessionScoped
public class PaymentAcknowledgeManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private Long paymentId;
	private boolean paymentStatus;

	public Long getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}
	
	public boolean isPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	
}
