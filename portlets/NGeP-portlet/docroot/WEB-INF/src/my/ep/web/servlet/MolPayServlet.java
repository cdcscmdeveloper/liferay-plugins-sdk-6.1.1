package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.PaymentUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import com.liferay.portal.util.PortalUtil;
//#26307 start
//#26307 start

/**
 * Servlet implementation class MolPayServlet
 */
//@WebServlet("/MolPayServlet")
public class MolPayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(MolPayServlet.class);
	private final String merchantId = EpPropertiesUtils.getInstance().getProperty("molpay.merchantid");
	private final String molIpnUrl = EpPropertiesUtils.getInstance().getProperty("molpay.ipnurl");
	private HttpServletRequest httpReq;

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

		//res.sendRedirect("http://localhost:8080/web/guest/svo-site-visit");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// #26307 start
		log.info("************Payment PostBack Start********");
		log.info("******Servlet Testing doPost*********");
		// #26307 end
		//26307
		log.info("******START Clear Cache*********");
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setDateHeader("Expires", 0); // Proxies.
		
		log.info("******END Clear Cache*********");
		//26307
		
		httpReq = req;
		Map<String,String> reqMap = getHttpReqMap();
		String nbcb = reqMap.get("nbcb");
		String amount = reqMap.get("amount");
		String orderId = reqMap.get("orderid");
		String appcode = reqMap.get("appcode");
		String tranID = reqMap.get("tranID");
		String domain = reqMap.get("domain");
		String status = reqMap.get("status");
		String errorCode = reqMap.get("error_code");
		String errorDesc = reqMap.get("error_desc");
		String currency = reqMap.get("currency");
		String paydate = reqMap.get("paydate");
		String skey = reqMap.get("skey");
		String channel = reqMap.get("channel");
		log.info("set session--");
		req.getSession().setAttribute("orderidS", orderId);
		log.info("getsession servlet: -"+req.getSession().getAttribute("orderidS"));
		// #26307 start
		String cardType = reqMap.get("cardType");
		String extraP = reqMap.get("extraP");
		
		String date1 = reqMap.get("date1");
		String molpayTransId = reqMap.get("molpayTransId");
		
		log.info("nbcb:" + nbcb);
		log.info("orderId: " + orderId);
		log.info("tranID: " + tranID);
		log.info("status:" + status);
		log.info("domain:" + domain);
		log.info("amount :" + amount);
		log.info("currency:" + currency);
		log.info("appcode: " + appcode);
		log.info("paydate:" + paydate);
		log.info("skey:" + skey);
		log.info("error_code:" + errorCode);
		log.info("error_desc:" + errorDesc);
		log.info("channel:" + channel);
		log.info("extraP:" + extraP);
		
		if(nbcb != null){
			Long ordId = Long.valueOf(orderId);
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setPaymentId(ordId);
			PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
														
			String vKey = SmCommonUtil.getInstance().getPaymentGatewayVKeyByBillType(resDTO.getPyPayment().getPyBill().getBillType());
			// To verify the data integrity sending by MOLPay
			String key0 = SmCommonUtil.md5(tranID + orderId + status + domain + amount + currency);
			String key1 = SmCommonUtil.md5(paydate + domain + key0 + appcode + vKey);

			log.info("SKey>>>>" + skey);
			log.info("Key1>>>>" + key1);
			if(skey.equals(key1) || reqMap.get("paymentGateway").equals("Y")){
				log.info("SKey & Key1 Are Match>>>>");

				BigDecimal txAmount = new BigDecimal(amount);
				boolean validPayment = false;

				if (resDTO.getPyPayment() != null) {
					log.info("MolPayServlet| getPaymentId >>>>" + resDTO.getPyPayment().getPaymentId());
					PyPayment smPayment = resDTO.getPyPayment();
					PyPaymentResponse smPaymentRes = new PyPaymentResponse();
					PyPaymentOrder paymentOrder = smPayment.getPyPaymentOrder();
					Long statusCode = null;

					if (status.equals(SupplierManagementConst.MOL_SUCCESS)) {
						BigDecimal paymentAmount = smPayment.getPaymentAmt();
						Long paymentId = smPayment.getPaymentId();

						if (paymentId.longValue() == ordId.longValue() && paymentAmount.compareTo(txAmount) == 0) {
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
							log.info("paymentAmount id - " + paymentAmount);
						}
					}else if(status.equals(SupplierManagementConst.MOL_FAIL)){
						statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;

					}else if(status.equals(SupplierManagementConst.MOL_PENDING)){
						statusCode = SupplierManagementConst.PM_STAT_PENDING_PAYMENT;
					}
					//32422 START
					Timestamp txDate = new Timestamp(System.currentTimeMillis());
					try {
						txDate = DateUtils.convertStringToTimestamp("yyyy-MM-dd HH:mm:ss", paydate);
					} catch (Exception e) {
						txDate = new Timestamp(System.currentTimeMillis());
						e.printStackTrace();
					}
					//32422 END
					// #26307 start
					PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
					log.info("Card type=============="+cardType);
					if (cardType != null) {
						if (cardType.equalsIgnoreCase("Credit")) {
							cardType = "CC";
						} else if (cardType.equalsIgnoreCase("Debit")) {
							cardType = "DC";
						} 
						log.info("cardType===="+cardType);
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
					//START THIS LINE OF CODE DO NOT MERGE TO PRODUCTION
					if(cardType == null && channel != null && !"credit".equalsIgnoreCase(channel) && !"debit".equalsIgnoreCase(channel)){
						channel = "FPX";
					}
					//END THIS LINE OF CODE DO NOT MERGE TO PRODUCTION
					// #26307 end
					//32422 START
					Timestamp bankDate = new Timestamp(System.currentTimeMillis());
					Boolean validFormat = false;
					try {
						if(date1.trim().equals("0000-00-00 00:00:00")){
							log.info("Invalid Date ==== "+date1);
							date1 = null;
						}
						if(date1 == null || date1.trim().equals("")){
							throw new Exception();
						}
						if(date1 != null){
							log.info("Bank Date&Time ==== "+date1);
							SimpleDateFormat sdfrmt = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
						    try{
						    	log.info("In try");
						        sdfrmt.parse(date1);
						        log.info("Setting Valid Format to true");
						        validFormat = true;
						    }
						    catch(ParseException e){
						    	log.info("In Parse Catch");
							    validFormat = false;
						    }
						    finally {
						    	log.info("In finally");
						    	if(validFormat){
						    		log.info("Valid Format");
									bankDate = DateUtils.convertStringToTimestamp("yyyy-MM-dd HH:mm:ss", date1);
									pymtReqDTO.setBankDate(bankDate);
								}else{
									log.info("Invalid Format");
									throw new Exception();
								}
						    }
						}
					} catch (Exception e) {
						log.info("bankDate is null");
						bankDate = null;
						e.printStackTrace();
					}
					//32422 END
					log.info("channel---------"+channel);
					paymentOrder.setChannelName(channel);
					
					//#33137 - start
					if(channel.equalsIgnoreCase("FPX")){
						smPaymentRes.setTransactionId(tranID);
						log.info("SET TRANSACTION ID--------------------33137"+tranID);
					}else{
						smPaymentRes.setTransactionId(molpayTransId);
						log.info("SET MOLPAY TRANS ID--------------------33137"+molpayTransId);
					}
					smPaymentRes.setMolpayTransactionId(molpayTransId);
					smPaymentRes.setFpxTranId(tranID);
					//#33137 - end
					smPaymentRes.setErrorCode(errorCode);
					smPaymentRes.setErrorDesc(errorDesc);
					//32422 START
					if(bankDate != null){
						smPaymentRes.setTransactionDate(bankDate);
					}else{
						smPaymentRes.setTransactionDate(txDate);
					}
					smPaymentRes.setBankDateTime(bankDate);
					//32422 END
					smPaymentRes.setPyPaymentOrder(paymentOrder);
					smPaymentRes.setPaymentStatus(status);
					smPaymentRes.setPaidAmt(txAmount);
					paymentOrder.getPyPaymentResponses().add(smPaymentRes);
					int requeryCount = paymentOrder.getRequeryCount() + 1;
					paymentOrder.setRequeryCount(requeryCount);

					if(statusCode != null){
						StatusReqDTO statusReqDTO = new StatusReqDTO();
						statusReqDTO.setStatusId(statusCode);
						PmStatus pmStat= SmServiceLookup.getStatusSO().retrieveStatusByStatusId(statusReqDTO).getPmStatus();
						smPayment.setPmStatus(pmStat);
					}
					
					

					
					//pymtReqDTO.setPaidPersonnels(getPaidPersonnelBeforeProceed());
					pymtReqDTO.setValidPayment(validPayment);
					pymtReqDTO.setPyPayment(smPayment);
					pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);

					//						pymtReqDTO.setTaskId(this.getManagedBean().getTaskId());
					//						pymtReqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
					//						pymtReqDTO.setToken(token);
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
					PaymentResDTO pymtResDTO = new PaymentResDTO();
//					PaymentResDTO pymtResDTO = reqSO.paymentBackendUpdate(pymtReqDTO);
					log.info(">>>>Before paymentBackendUpdate<<<");
					if(pymtResDTO.isSuccessful()){
						if(nbcb.equals("1")){
							//to auto generate mof cert
							if(smPayment.getPyBill().getBillType().
									equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE) && 
									validPayment){
								/*try{
									SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(smPayment.getPyBill().getBillRefId());
								}catch(Exception ex){
									log.info("<<<<MOLPayment - error when generate Virtual Cert>>>>" + ex.getMessage());
								}*/
							}
							
							log.info("Curl IPN - CBTOKEN:MPSTATOK " + nbcb);
							PrintWriter out = res.getWriter();
							try{
								out.print("CBTOKEN:MPSTATOK");
							}finally{
								out.close();
							}
							
						}else if(nbcb.equals("2")){
							log.info("Curl IPN - nbcb " + nbcb);
							try {
								SmCommonUtil.paymentCurlIPN(req, true, merchantId, molIpnUrl);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								log.info("paymentCurlIPN error>>>>>>>>>" + e.getMessage());
							}
						}
					}
				}else{
					log.info("No SmPayment Object is found");
				}

				//			}else{
				//				log.info("*******SKey Unmatch***********");
				//			}
			}
		}

		/*	private void updateRecordStatus(BaseEntity entity) {
		entity.setCreatedBy(1L);
		entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		entity.setChangedBy(1L);
	}*/

	}
	
	private Map<String,String> getHttpReqMap(){
		String paymentGateway = "N";
		String payload = httpReq.getParameter("payload");
		String nbcb = httpReq.getParameter("nbcb") != null ? httpReq.getParameter("nbcb") : null;
		String amount = httpReq.getParameter("amount");
		String orderId = httpReq.getParameter("orderid");
		String appcode = httpReq.getParameter("appcode");
		String tranID = httpReq.getParameter("tranID");
		String domain = httpReq.getParameter("domain");
		String status = httpReq.getParameter("status");
		String errorCode = httpReq.getParameter("error_code") != null ?  httpReq.getParameter("error_code") : null;
		String errorDesc = httpReq.getParameter("error_desc") != null ?  httpReq.getParameter("error_desc") : null;
		String currency = httpReq.getParameter("currency");
		String paydate = httpReq.getParameter("paydate");
		String skey = httpReq.getParameter("skey");
		String channel = httpReq.getParameter("channel") != null ?  httpReq.getParameter("channel") : null;
		
		String cardType = null;
		String extraP = httpReq.getParameter("extraP");
		
		JSONObject obj; 
		try { 
			obj = new JSONObject(extraP); 
			cardType = obj.getString("cctype") != null ? obj.getString("cctype") : null; 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		// #26307 end  
		
		// #32422 START 
		//#33137 - START
		String date1 = null;
		String molpayTransId = null;
		String extraP1 = httpReq.getParameter("extraP");
		JSONObject obj1;
		log.info("######38377#####extraP1 before:" + extraP1);
		try { 
			obj1 = new JSONObject(extraP1); 
			log.info("######38377#####extraP1 after:" + extraP1);
			
			if(obj1.has("bank_datetime")){
				log.info("######38377#####bank date time before set -----");
				date1 = obj1.getString("bank_datetime") != null ? obj1.getString("bank_datetime") : null;
			}
			
			if(obj1.has("MOLPay_tranID")){
				molpayTransId = obj1.getString("MOLPay_tranID") != null ? obj1.getString("MOLPay_tranID") : null;
				log.info("######38377#####molpay id after : "+ molpayTransId);	
			}
		} catch (JSONException e) { 
			log.info("######38377#####molpay id catch : "+molpayTransId);
			e.printStackTrace(); 
		}
		
		JSONObject jsonObject = PaymentUtil.getPaymentGatewayJson(payload);
		if (jsonObject != null) {
			try {
				orderId = jsonObject.getString("orderId");
				tranID = jsonObject.getString("transId");
				status = jsonObject.getString("status");
				paydate = jsonObject.getString("trxDate");
				channel = jsonObject.getString("channel");
				errorCode = jsonObject.getString("errCode");
				errorDesc = jsonObject.getString("errDesc");
				currency = jsonObject.getString("currency");
				if (channel
						.equals(SupplierManagementConst.PAYMENT_GATEWAY_CHANNEL_MGATE)) {
					cardType = ParameterConst.CARD_TYPES_CREDIT; 
				}else{
					molpayTransId = tranID;
				}
				date1 = paydate;
				paymentGateway = "Y";
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		Map<String,String> reqMap = new HashMap<>();
		reqMap.put("nbcb", nbcb);
		reqMap.put("amount", amount);
		reqMap.put("orderId", orderId);
		reqMap.put("appcode", appcode);
		reqMap.put("tranID", tranID);
		reqMap.put("domain", domain);
		reqMap.put("status", status);
		reqMap.put("errorCode", errorCode);
		reqMap.put("errorDesc", errorDesc);
		reqMap.put("currency", currency);
		reqMap.put("paydate", paydate);
		reqMap.put("skey", skey);
		reqMap.put("channel", channel);
		reqMap.put("extraP", extraP);
		reqMap.put("date1", date1);
		reqMap.put("cardType", cardType);
		reqMap.put("molpayTransId", molpayTransId);
		reqMap.put("paymentGateway", paymentGateway);
		
		log.info("######38377#####nbcb:" + reqMap.get("nbcb"));
		log.info("######38377#####orderId: " + reqMap.get("orderId"));
		log.info("######38377#####tranID: " + reqMap.get("tranID"));
		log.info("######38377#####status:" + reqMap.get("status"));
		log.info("######38377#####domain:" + reqMap.get("domain"));
		log.info("######38377#####amount :" + reqMap.get("amount"));
		log.info("######38377#####currency:" + reqMap.get("currency"));
		log.info("######38377#####appcode: " + reqMap.get("appcode"));
		log.info("######38377#####paydate:" + reqMap.get("paydate"));
		log.info("######38377#####skey:" + reqMap.get("skey"));
		log.info("######38377#####error_code:" + reqMap.get("error_code"));
		log.info("######38377#####error_desc:" + reqMap.get("error_desc"));
		log.info("######38377#####channel:" + reqMap.get("channel"));
		log.info("######38377#####extraP:" + reqMap.get("extraP"));
		log.info("######38377#####date1:" + reqMap.get("date1"));
		log.info("######38377#####cardType:" + reqMap.get("cardType"));
		log.info("######38377#####molpayTransId:" + reqMap.get("molpayTransId"));
		log.info("######38377#####paymentGateway:" + reqMap.get("paymentGateway"));
		
		return reqMap;
	}
}
