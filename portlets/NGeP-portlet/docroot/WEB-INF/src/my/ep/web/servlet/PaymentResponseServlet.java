package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.AESEncryptor;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.PaymentUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;

import com.liferay.portal.util.PortalUtil;
//#26307 start
//#26307 start

/**
 * Servlet implementation class MolPayServlet
 */
//@WebServlet("/MolPayServlet")
public class PaymentResponseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(PaymentResponseServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("******Servlet Testing doGet*********");
		PrintWriter out = res.getWriter();
		try{
			out.print("CBTOKEN:MPSTATOK");
		}finally{
			out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("************New Payment PostBack Start********");
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setDateHeader("Expires", 0); // Proxies.
		String payload = req.getParameter("payload");
		String orderId = StringUtils.EMPTY;
        String status = StringUtils.EMPTY;
        String paydate = StringUtils.EMPTY;
        String paymentMethod = StringUtils.EMPTY;
        String channel = StringUtils.EMPTY;
        String errorCode = StringUtils.EMPTY;
        String errorDesc = StringUtils.EMPTY;
		String appcode = StringUtils.EMPTY;
		String tranID = StringUtils.EMPTY;
		boolean isValidPayload = false;
		try {
			payload = AESEncryptor
					.decrypt(
							payload,
							SmCommonUtil
									.getInstance()
									.getCDCPaymentGatewayVKeyByBillType(
											my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE));
			JSONObject jsonObject = new JSONObject(payload);
			
			orderId = jsonObject.getString("orderId");
			tranID = jsonObject.getString("transId");
	        status = jsonObject.getString("status");
	        paydate = jsonObject.getString("trxDate");
	        paymentMethod = jsonObject.getString("paymentType");
	        channel = jsonObject.getString("channel");
	        errorCode = jsonObject.getString("errCode");
	        errorDesc = jsonObject.getString("errDesc");
	        isValidPayload = true;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		req.getSession().setAttribute("orderidS", orderId);
		log.info("getsession servlet: -"+req.getSession().getAttribute("orderidS"));
		// #26307 start
		String cardType = null;
		if(channel.equals(SupplierManagementConst.PAYMENT_GATEWAY_CHANNEL_MGATE)){
			cardType = ParameterConst.CARD_TYPES_CREDIT;
		}
		
		log.info("orderId: " + orderId);
		log.info("tranID: " + tranID);
		log.info("status:" + status);
		log.info("appcode: " + appcode);
		log.info("paydate:" + paydate);
		log.info("error_code:" + errorCode);
		log.info("error_desc:" + errorDesc);
		log.info("channel:" + channel);
		log.info("payment method:" + paymentMethod);
		
			Long ordId = Long.valueOf(orderId);
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setPaymentId(ordId);
			PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
			
			if(isValidPayload){
				boolean validPayment = false;

				if (resDTO.getPyPayment() != null) {
					log.info("MolPayServlet| getPaymentId >>>>" + resDTO.getPyPayment().getPaymentId());
					PyPayment smPayment = resDTO.getPyPayment();
					PyPaymentResponse smPaymentRes = new PyPaymentResponse();
					PyPaymentOrder paymentOrder = smPayment.getPyPaymentOrder();
					Long statusCode = null;

					if (status.equals(SupplierManagementConst.PAYMENT_GATEWAY_SUCCESS)) {
						Long paymentId = smPayment.getPaymentId();

						if (paymentId.longValue() == ordId.longValue()) {
							if (smPayment.getPmStatus() != null) {
								if (smPayment.getPmStatus().getStatusId() == SupplierManagementConst.PM_STAT_PENDING_PAYMENT
										|| smPayment.getPmStatus().getStatusId() == SupplierManagementConst.PM_STAT_FAILED_PAYMENT) {
									statusCode = SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT;
									validPayment = true;
								}else{
									log.info("Payment Status ID -" + smPayment.getPmStatus().getStatusId());
								}
							}else{
								log.info("smPayment.getPmStatus() is NULL");
							}
						}else{
							log.info("ord id or payment amount unmatch");
							log.info("ord id - " + ordId);
							log.info("paymentAmount id - " + smPayment.getPaymentAmt());
						}
					}else if(status.equals(SupplierManagementConst.PAYMENT_GATEWAY_FAIL)){
						statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;

					}else if(status.equals(SupplierManagementConst.PAYMENT_GATEWAY_PENDING)){
						statusCode = SupplierManagementConst.PM_STAT_PENDING_PAYMENT;
					}
					//32422 END
					// #26307 start
					PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
					log.info("Card type=============="+cardType);
					if (cardType != null) {
						ParameterReqDTO reqPmDTO = new ParameterReqDTO();
						if (cardType.equals(ParameterConst.CARD_TYPES_CREDIT)) {	
							log.info("cardType====enter CC"+cardType);
							reqPmDTO.setParameterType(ParameterTypeConst.CARD_TYPES);
							reqPmDTO.setParameterCode(ParameterConst.CARD_TYPES_CREDIT);
							ParameterResDTO resPmDTO = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqPmDTO);
							log.info("cardType====enter CC result "+resPmDTO.getPmParameter());
							PmParameter cards = resPmDTO.getPmParameter();
							pymtReqDTO.setCardType(cards);
							pymtReqDTO.setCardTypeS(cardType);
							smPayment.setCardType(cards);	
							channel="credit";
						} else if (cardType.equals(ParameterConst.CARD_TYPES_DEBIT)) {		
							log.info("cardType====enter DC"+cardType);
							reqPmDTO.setParameterType(ParameterTypeConst.CARD_TYPES);
							reqPmDTO.setParameterCode(ParameterConst.CARD_TYPES_DEBIT);
							ParameterResDTO resPmDTO = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqPmDTO);
							log.info("cardType====enter DC result "+resPmDTO.getPmParameter());
							PmParameter cards = resPmDTO.getPmParameter();
							pymtReqDTO.setCardType(cards);
							pymtReqDTO.setCardTypeS(cardType);
							smPayment.setCardType(cards);	
							channel="debit";
						} 
					}
					
					//32422 START
					Timestamp bankDate = PaymentUtil.getFormattedTrxdate(paydate);
					log.info("channel---------"+channel);
					paymentOrder.setChannelName(channel);
					
					smPaymentRes.setTransactionId(tranID);
					smPaymentRes.setMolpayTransactionId(tranID);
					smPaymentRes.setFpxTranId(tranID);
					//#33137 - end
					smPaymentRes.setErrorCode(errorCode);
					smPaymentRes.setErrorDesc(errorDesc);
					//32422 START
					smPaymentRes.setTransactionDate(bankDate);
					smPaymentRes.setBankDateTime(bankDate);
					//32422 END
					smPaymentRes.setPyPaymentOrder(paymentOrder);
					smPaymentRes.setPaymentStatus(status);
					smPaymentRes.setPaidAmt(smPayment.getPaymentAmt());
					paymentOrder.getPyPaymentResponses().add(smPaymentRes);
					int requeryCount = paymentOrder.getRequeryCount() + 1;
					paymentOrder.setRequeryCount(requeryCount);

					if(statusCode != null){
						StatusReqDTO statusReqDTO = new StatusReqDTO();
						statusReqDTO.setStatusId(statusCode);
						PmStatus pmStat= SmServiceLookup.getStatusSO().retrieveStatusByStatusId(statusReqDTO).getPmStatus();
						smPayment.setPmStatus(pmStat);
					}
					pymtReqDTO.setValidPayment(validPayment);
					pymtReqDTO.setPyPayment(smPayment);
					pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
					String portalUrl = PortalUtil.getPortalURL(req);
					pymtReqDTO.setPortalBaseUrl(portalUrl);
					pymtReqDTO.setLanguage("en");

					log.info(">>>>Before paymentBackendUpdate Upper<<<");
					String paymentType = StringUtils.EMPTY;
					if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE)){
						paymentType = SupplierManagementConst.MOF_PAYMENT;
						log.info(">>>>paymentType<<<"+paymentType);
					}else if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
						paymentType = SupplierManagementConst.PROCESSING_PAYMENT;
						log.info(">>>>paymentType<<<"+paymentType);
					}else if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)){
						paymentType = SupplierManagementConst.SOFTCERT_PAYMENT;
						//START 35182
						smPayment.setReceiptFileName(ReportConst.SM_TAX_INVOICE_SST);
						//END 35182
						log.info(">>>>paymentType<<<"+paymentType);
					}
					pymtReqDTO.setPaymentType(paymentType);
					pymtReqDTO.setMethodName("doPost-MolPayServlet");
					PaymentResDTO pymtResDTO = reqSO.paymentBackendUpdate(pymtReqDTO);
					log.info(">>>>Before paymentBackendUpdate<<<");
				}else{
					log.info("No SmPayment Object is found");
				}
			}

	}
	
}
