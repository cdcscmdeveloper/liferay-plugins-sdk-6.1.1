package my.ep.web.backingbean.sm.payment;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class PaymentBean {

	private String paymentTaskId;
	
	private Object paymentBpmPojo;

	public String getPaymentTaskId() {
		return paymentTaskId;
	}

	public void setPaymentTaskId(String paymentTaskId) {
		this.paymentTaskId = paymentTaskId;
	}

	public Object getPaymentBpmPojo() {
		return paymentBpmPojo;
	}

	public void setPaymentBpmPojo(Object paymentBpmPojo) {
		this.paymentBpmPojo = paymentBpmPojo;
	}
	
	
}
