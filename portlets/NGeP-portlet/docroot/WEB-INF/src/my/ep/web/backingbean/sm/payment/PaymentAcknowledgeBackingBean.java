package my.ep.web.backingbean.sm.payment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.payment.PaymentAcknowledgeManagedBean;

@ManagedBean(name = "paymentAcknowledgeBB")
@RequestScoped
public class PaymentAcknowledgeBackingBean extends BaseBackingBean<PaymentAcknowledgeManagedBean>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(PaymentAcknowledgeBackingBean.class);

	public PaymentAcknowledgeBackingBean(){
		
	}
	
	public void printReceipt() throws IOException{
		log.info("printReceipt>>>>" + this.getManagedBean().getPaymentId());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_PAYMENT_ID, this.getManagedBean().getPaymentId() + "");

		generateReport(ReportConst.SM_PAYMENT_RECEIPT_JSP, paramMap);
	}
}
