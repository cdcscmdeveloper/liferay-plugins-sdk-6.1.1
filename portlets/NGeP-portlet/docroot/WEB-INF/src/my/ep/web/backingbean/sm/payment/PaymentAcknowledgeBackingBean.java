package my.ep.web.backingbean.sm.payment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.payment.PaymentAcknowledgeManagedBean;

@ManagedBean (name = "paymentAcknowledgeBackingBean")
@RequestScoped
public class PaymentAcknowledgeBackingBean extends BaseBackingBean<PaymentAcknowledgeManagedBean>{

	public PaymentAcknowledgeBackingBean(){
		
	}
	
	public void printReceipt() throws IOException{
		//String paymentId = this.getManagedBean().getPaymentId().toString();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_PAYMENT_ID, this.getManagedBean().getPaymentId() + "");
		
		generateReport(ReportConst.SM_PAYMENT_RECEIPT_JSP, paramMap);
	}
}
