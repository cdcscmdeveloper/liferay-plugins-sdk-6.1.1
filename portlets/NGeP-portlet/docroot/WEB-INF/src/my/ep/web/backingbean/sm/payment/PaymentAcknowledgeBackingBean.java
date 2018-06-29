package my.ep.web.backingbean.sm.payment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.payment.PaymentAcknowledgeManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	
	public void printReceipt() throws IOException {
		log.info("printReceipt>>>>" + this.getManagedBean().getPaymentId());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_PAYMENT_ID, this.getManagedBean().getPaymentId() + "");

		//Enhancement #19056 To replace Receipt document format for SPKI purchase to Tax Invoice format
		IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		reqDTO.setPaymentId(this.getManagedBean().getPaymentId());
		
		PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
		String billType = StringUtils.EMPTY;
		billType = resDTO.getPyPayment().getPyBill().getBillType(); //billtype = R/P/S
		log.info(":::printReceipt() paymentId: "+ this.getManagedBean().getPaymentId() +". billType: "+ billType);
		
		if (billType.equalsIgnoreCase(
						my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE) || 
			billType.equalsIgnoreCase(
						my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)) { //R/P
			generateReport(ReportConst.SM_PAYMENT_RECEIPT_JSP, paramMap);
		} else if (billType.equalsIgnoreCase(
						my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)) { //S
	        generateReport(ReportConst.SM_TAX_INVOICE_JSP, paramMap);
		}		
		//end of Enhancement #19056
	}
}
