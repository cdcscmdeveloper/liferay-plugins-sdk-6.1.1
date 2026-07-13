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
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.PaymentUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;

import com.liferay.portal.util.PortalUtil;

/**
 * Servlet implementation class MolPayServlet
 */
// @WebServlet("/MolPayServlet")
public class PaymentCallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(PaymentCallbackServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("******Servlet Testing doGet*********");
		PrintWriter out = res.getWriter();
		try {
			out.print("Hello World!!");
		} finally {
			out.close();
		}

		// res.sendRedirect("http://localhost:8080/web/guest/svo-site-visit");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// #26307 start
		log.info("************Payment PostBack Start********");
		log.info("******Servlet Testing doPost*********");
		// #26307 end
		// 26307
		log.info("******START Clear Cache*********");
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
																				// 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setDateHeader("Expires", 0); // Proxies.

		log.info("******END Clear Cache*********");

		// #38377
		Map<String, String> reqMap = getHttpReqMap(req);
		String amount = reqMap.get("amount");
		String orderId = reqMap.get("orderid");
		String tranID = reqMap.get("tranID");
		String status = reqMap.get("status");
		String errorCode = reqMap.get("error_code");
		String errorDesc = reqMap.get("error_desc");
		String currency = reqMap.get("currency");
		String paydate = reqMap.get("paydate");
		String channel = reqMap.get("channel");
		String paymentRefNo = reqMap.get("paymentRefNo");
		String bankName = reqMap.get("bankName");
		String providerTransId = reqMap.get("providerTransId");
		String customerEmail = reqMap.get("customerEmail");
		String customerContactNo = reqMap.get("customerContactNo");

		// DO NOT INCLUDE IN PROD because only to generate dummy excel in
		// SIT/UAT
		String cardMaskedNum = reqMap.get("cardMaskedNum");

		log.info("set session--");
		req.getSession().setAttribute("orderidS", orderId);
		log.info("getsession servlet: -" + req.getSession().getAttribute("orderidS"));
		String cardType = reqMap.get("cardType");
		String date1 = reqMap.get("date1");

		Long ordId = Long.valueOf(orderId);
		IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		reqDTO.setPaymentId(ordId);
		PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);

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
						} else {
							log.info("Payment Status ID -" + smPayment.getPmStatus().getStatusId());
						}
					} else {
						log.info("smPayment.getPmStatus() is NULL");
					}
				} else {
					log.info("ord id or payment amount unmatch");
					log.info("ord id - " + ordId);
					log.info("paymentAmount id - " + paymentAmount);
				}
			} else if (status.equals(SupplierManagementConst.MOL_FAIL)) {
				statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;

			} else if (status.equals(SupplierManagementConst.MOL_PENDING)) {
				statusCode = SupplierManagementConst.PM_STAT_PENDING_PAYMENT;
			}
			// 32422 START
			Timestamp txDate = new Timestamp(System.currentTimeMillis());
			try {
				txDate = DateUtils.convertStringToTimestamp("yyyy-MM-dd HH:mm:ss", paydate);
			} catch (Exception e) {
				txDate = new Timestamp(System.currentTimeMillis());
				e.printStackTrace();
			}
			// 32422 END
			// #26307 start
			PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
			// #38377
			pymtReqDTO.setCustomerEmail(customerEmail);
			pymtReqDTO.setContactNo(customerContactNo);

			log.info("Card type==============" + cardType);
			if (cardType != null) {
				if (cardType.equalsIgnoreCase("Credit")) {
					cardType = "CC";
				} else if (cardType.equalsIgnoreCase("Debit") || cardType.equalsIgnoreCase("Prepaid")) { // #39654
																											// -
																											// add
																											// prepaid
					cardType = "DC";
				}
				log.info("cardType====" + cardType);
				ParameterReqDTO reqPmDTO = new ParameterReqDTO();
				if (cardType.equals(ParameterConst.CARD_TYPES_CREDIT)) {
					log.info("cardType====enter CC" + cardType);
					reqPmDTO.setParameterType(ParameterTypeConst.CARD_TYPES);
					reqPmDTO.setParameterCode(ParameterConst.CARD_TYPES_CREDIT);
					ParameterResDTO resPmDTO = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqPmDTO);
					log.info("cardType====enter CC result " + resPmDTO.getPmParameter());
					PmParameter cards = resPmDTO.getPmParameter();
					pymtReqDTO.setCardType(cards);
					pymtReqDTO.setCardTypeS(cardType);
					smPayment.setCardType(cards);
					channel = "credit";
				} else if (cardType.equals(ParameterConst.CARD_TYPES_DEBIT)) {
					log.info("cardType====enter DC" + cardType);
					reqPmDTO.setParameterType(ParameterTypeConst.CARD_TYPES);
					reqPmDTO.setParameterCode(ParameterConst.CARD_TYPES_DEBIT);
					ParameterResDTO resPmDTO = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqPmDTO);
					log.info("cardType====enter DC result " + resPmDTO.getPmParameter());
					PmParameter cards = resPmDTO.getPmParameter();
					pymtReqDTO.setCardType(cards);
					pymtReqDTO.setCardTypeS(cardType);
					smPayment.setCardType(cards);
					channel = "debit";
				}
			}
			// START THIS LINE OF CODE DO NOT MERGE TO PRODUCTION
			if (cardType == null && channel != null && !"credit".equalsIgnoreCase(channel) && !"debit".equalsIgnoreCase(channel)) {
				channel = "FPX";
			}
			// END THIS LINE OF CODE DO NOT MERGE TO PRODUCTION
			// #26307 end
			// 32422 START
			Timestamp bankDate = new Timestamp(System.currentTimeMillis());
			Boolean validFormat = false;
			try {
				if (date1.trim().equals("0000-00-00 00:00:00")) {
					log.info("Invalid Date ==== " + date1);
					date1 = null;
				}
				if (date1 == null || date1.trim().equals("")) {
					throw new Exception();
				}
				if (date1 != null) {
					log.info("Bank Date&Time ==== " + date1);
					SimpleDateFormat sdfrmt = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
					try {
						log.info("In try");
						sdfrmt.parse(date1);
						log.info("Setting Valid Format to true");
						validFormat = true;
					} catch (ParseException e) {
						log.info("In Parse Catch");
						validFormat = false;
					} finally {
						log.info("In finally");
						if (validFormat) {
							log.info("Valid Format");
							bankDate = DateUtils.convertStringToTimestamp("yyyy-MM-dd HH:mm:ss", date1);
							pymtReqDTO.setBankDate(bankDate);
						} else {
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
			// 32422 END
			log.info("channel---------" + channel);
			paymentOrder.setChannelName(channel);

			smPaymentRes.setTransactionId(tranID);
			// #33137 - end
			smPaymentRes.setErrorCode(errorCode);
			smPaymentRes.setErrorDesc(errorDesc);
			smPaymentRes.setTransactionDate(txDate);
			smPaymentRes.setBankDateTime(txDate);
			// 32422 END
			smPaymentRes.setPyPaymentOrder(paymentOrder);
			smPaymentRes.setPaymentStatus(status);
			smPaymentRes.setPaidAmt(txAmount);
			paymentOrder.getPyPaymentResponses().add(smPaymentRes);
			int requeryCount = paymentOrder.getRequeryCount() + 1;
			paymentOrder.setRequeryCount(requeryCount);

			if (statusCode != null) {
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(statusCode);
				PmStatus pmStat = SmServiceLookup.getStatusSO().retrieveStatusByStatusId(statusReqDTO).getPmStatus();
				smPayment.setPmStatus(pmStat);
			}

			// pymtReqDTO.setPaidPersonnels(getPaidPersonnelBeforeProceed());
			pymtReqDTO.setValidPayment(validPayment);
			pymtReqDTO.setPyPayment(smPayment);
			pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);

			// pymtReqDTO.setTaskId(this.getManagedBean().getTaskId());
			// pymtReqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			// pymtReqDTO.setToken(token);
			String portalUrl = PortalUtil.getPortalURL(req);
			pymtReqDTO.setPortalBaseUrl(portalUrl);
			pymtReqDTO.setLanguage("en");

			log.info(">>>>Before paymentBackendUpdate Upper<<<");
			String paymentType = StringUtils.EMPTY;
			if (smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE)) {
				paymentType = SupplierManagementConst.MOF_PAYMENT;
				log.info(">>>>paymentType<<<" + paymentType);
			} else if (smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)) {
				paymentType = SupplierManagementConst.PROCESSING_PAYMENT;
				log.info(">>>>paymentType<<<" + paymentType);
			} else if (smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)) {
				paymentType = SupplierManagementConst.SOFTCERT_PAYMENT;
				// START 35182
				smPayment.setReceiptFileName(ReportConst.SM_TAX_INVOICE_SST);
				// END 35182
				log.info(">>>>paymentType<<<" + paymentType);
			}
			pymtReqDTO.setPaymentType(paymentType);
			pymtReqDTO.setMethodName("doPost-MolPayServlet");

			PaymentResDTO pymtResDTO = new PaymentResDTO();

			// #38377 send success payment email (status code null means
			// already got success payment for this payment id)
			log.info(">>>>before send success email payment statusCode:" + statusCode + "<<<<<");
			if (statusCode != null && statusCode == SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT) {
				log.info(">>>>start send success email payment<<<");
				pymtResDTO = reqSO.saveSuccessPaymentEmailTemplateParams(pymtReqDTO);
				pymtReqDTO.setNotifyMessageId(pymtResDTO.getNotifyMessageId());
				reqSO.sendSuccessPaymentEmail(pymtReqDTO);
				
				// #38377 status code null means already got success payment
				// for this payment id so no need to save to avoid duplicate
				// resit
				pymtResDTO = reqSO.paymentBackendUpdate(pymtReqDTO);
			}
			printSuccessResponseCode(statusCode, pymtResDTO.isSuccessful(), res.getWriter());
		} else {
			log.info("No SmPayment Object is found");
		}

	}

	private Map<String, String> getHttpReqMap(HttpServletRequest httpReq) {
		String payload = httpReq.getParameter("payload");
		log.info("#38377########payload:" + payload);
		String amount = httpReq.getParameter("amount");
		String orderId = httpReq.getParameter("orderid");
		String tranID = httpReq.getParameter("tranID");
		String status = httpReq.getParameter("status");
		String errorCode = httpReq.getParameter("error_code") != null ? httpReq.getParameter("error_code") : null;
		String errorDesc = httpReq.getParameter("error_desc") != null ? httpReq.getParameter("error_desc") : null;
		String currency = httpReq.getParameter("currency");
		String paydate = httpReq.getParameter("paydate");
		String channel = httpReq.getParameter("channel") != null ? httpReq.getParameter("channel") : null;

		String cardType = null;

		// #32422 START
		// #33137 - START
		String date1 = null;

		String paymentRefNo = "";
		String bankName = "";
		String providerTransId = "";
		String customerEmail = "";
		String cardMaskedNum = "";
		String customerContactNo = "";
		JSONObject jsonObject = PaymentUtil.getPaymentGatewayJson(payload);
		if (jsonObject != null) {
			try {
				paymentRefNo = jsonObject.optString("paymentRefNo", null);
				log.info("######38377#####json paymentRefNo: " + paymentRefNo);
				providerTransId = jsonObject.optString("providerTransId", null);
				providerTransId = providerTransId == null ? paymentRefNo : providerTransId;
				log.info("######38377#####json providerTransId: " + providerTransId);
				bankName = jsonObject.optString("bankName", null);
				log.info("######38377#####json bankName: " + bankName);
				orderId = jsonObject.optString("orderId", null);
				log.info("######38377#####json orderId: " + orderId);
				tranID = jsonObject.optString("transId", null);
				log.info("######38377#####json tranID: " + tranID);
				status = jsonObject.optString("status", null);
				log.info("######38377#####json status: " + status);
				paydate = jsonObject.optString("trxDate", null);
				log.info("######38377#####json paydate: " + paydate);
				channel = jsonObject.optString("channel", null);
				log.info("######38377#####json channel: " + channel);
				amount = jsonObject.optString("amount", null);
				log.info("######38377#####json amount: " + amount);
				currency = jsonObject.optString("currency", null);
				log.info("######38377#####json currency: " + currency);
				customerEmail = jsonObject.optString("customerEmail", null);
				log.info("######38377#####json customerEmail: " + customerEmail);
				customerContactNo = jsonObject.optString("customerContactNo", null);
				log.info("######38377#####json customerContactNo: " + customerContactNo);
				if (SupplierManagementConst.PAYMENT_GATEWAY_CHANNEL_MGATE.equals(channel)) {
					cardType = ParameterConst.CARD_TYPES_CREDIT;

					// do not include to PROD. Used for generate dummy
					// transaction list excel
					cardMaskedNum = jsonObject.optString("cardMaskedNum", null);
					log.info("######38377#####json cardMaskedNum: " + cardMaskedNum);

					log.info("######38377#####json cardType: " + cardType);
				}
				date1 = paydate;
				errorCode = jsonObject.optString("errCode", null);
				log.info("######38377#####json errCode: " + errorCode);
				errorDesc = jsonObject.optString("errDesc", null);
				log.info("######38377#####json errorDesc: " + errorDesc);
			} catch (Exception e1) {
				log.info("######38377#####error get payload" + e1.getMessage());
			}
		}

		Map<String, String> reqMap = new HashMap<>();
		reqMap.put("amount", amount);
		reqMap.put("orderid", orderId);
		reqMap.put("tranID", tranID);
		reqMap.put("status", status);
		reqMap.put("error_code", errorCode);
		reqMap.put("error_desc", errorDesc);
		reqMap.put("currency", currency);
		reqMap.put("paydate", paydate);
		reqMap.put("channel", channel);
		reqMap.put("date1", date1);
		reqMap.put("cardType", cardType);
		reqMap.put("paymentRefNo", paymentRefNo);
		reqMap.put("providerTransId", providerTransId);
		reqMap.put("bankName", bankName);
		reqMap.put("customerEmail", customerEmail);
		reqMap.put("customerContactNo", customerContactNo);

		// do not include to PROD. Used for generate dummy transaction list
		// excel
		reqMap.put("cardMaskedNum", cardMaskedNum);

		log.info("######38377#####orderId: " + reqMap.get("orderid"));
		log.info("######38377#####tranID: " + reqMap.get("tranID"));
		log.info("######38377#####status:" + reqMap.get("status"));
		log.info("######38377#####amount :" + reqMap.get("amount"));
		log.info("######38377#####currency:" + reqMap.get("currency"));
		log.info("######38377#####paydate:" + reqMap.get("paydate"));
		log.info("######38377#####error_code:" + reqMap.get("error_code"));
		log.info("######38377#####error_desc:" + reqMap.get("error_desc"));
		log.info("######38377#####channel:" + reqMap.get("channel"));
		log.info("######38377#####date1:" + reqMap.get("date1"));
		log.info("######38377#####cardType:" + reqMap.get("cardType"));
		log.info("######38377#####bankName:" + reqMap.get("bankName"));
		log.info("######38377#####paymentRefNo:" + reqMap.get("paymentRefNo"));
		log.info("######38377#####providerTransId:" + reqMap.get("providerTransId"));
		log.info("######38377#####customerEmail:" + reqMap.get("customerEmail"));
		log.info("######38377#####customerContactNo:" + reqMap.get("customerContactNo"));

		// do not include to PROD. Used for generate dummy transaction list
		// excel
		log.info("######38377#####cardMaskedNum:" + reqMap.get("cardMaskedNum"));

		return reqMap;
	}

	private void printSuccessResponseCode(Long statusCode, boolean isSuccessSave, PrintWriter out) {
		log.info("#38377########printSuccessResponseCode:" + statusCode + "||" + isSuccessSave);

		if (isSuccessSave || statusCode == null) {
			try {
				out.print(SupplierManagementConst.MOL_SUCCESS);
				log.info("#38377########response code:" + SupplierManagementConst.MOL_SUCCESS);
			} finally {
				out.close();
			}
		}

	}
}
