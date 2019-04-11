package my.ep.web.backingbean.sm.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class PaymentBean {

//	private String paymentTaskId;
//	
//	private Object paymentBpmPojo;
	
	private Map<String, List<Object>> paymentMap = new HashMap<String, List<Object>>();

//	public String getPaymentTaskId() {
//		return paymentTaskId;
//	}
//
//	public void setPaymentTaskId(String paymentTaskId) {
//		this.paymentTaskId = paymentTaskId;
//	}
//
//	public Object getPaymentBpmPojo() {
//		return paymentBpmPojo;
//	}
//
//	public void setPaymentBpmPojo(Object paymentBpmPojo) {
//		this.paymentBpmPojo = paymentBpmPojo;
//	}

	public Map<String, List<Object>> getPaymentMap() {
		return paymentMap;
	}

	public void setPaymentMap(Map<String, List<Object>> paymentMap) {
		this.paymentMap = paymentMap;
	}
	
	
}
