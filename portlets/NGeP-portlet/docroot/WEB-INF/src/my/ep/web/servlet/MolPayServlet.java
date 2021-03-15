package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

//#26307
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ParameterConst;

import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;

import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

/**
 * Servlet implementation class MolPayServlet
 */
//@WebServlet("/MolPayServlet")
public class MolPayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(MolPayServlet.class);
	private final String merchantId = EpPropertiesUtils.getInstance().getProperty("molpay.merchantid");
	private final String molIpnUrl = EpPropertiesUtils.getInstance().getProperty("molpay.ipnurl");

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
		System.out.println("************Payment PostBack Start********");
		System.out.println("******Servlet Testing doPost*********");

		String nbcb = req.getParameter("nbcb") != null ? req.getParameter("nbcb") : null;
		String amount = req.getParameter("amount");
		String orderId = req.getParameter("orderid");
		String appcode = req.getParameter("appcode");
		String tranID = req.getParameter("tranID");
		String domain = req.getParameter("domain");
		String status = req.getParameter("status");
		String errorCode = req.getParameter("error_code") != null ?  req.getParameter("error_code") : null;
		String errorDesc = req.getParameter("error_desc") != null ?  req.getParameter("error_desc") : null;
		String currency = req.getParameter("currency");
		String paydate = req.getParameter("paydate");
		String skey = req.getParameter("skey");
		String channel = req.getParameter("channel") != null ?  req.getParameter("channel") : null;
		
		//#26307
		String cardType = req.getParameter("cardType") != null ?  req.getParameter("cardType") : null;

		System.out.println("nbcb:" + nbcb);
		System.out.println("orderId: " + orderId);
		System.out.println("tranID: " + tranID);
		System.out.println("status:" + status);
		System.out.println("domain:" + domain);
		System.out.println("amount :" + amount);
		System.out.println("currency:" + currency);
		System.out.println("appcode: " + appcode);
		System.out.println("paydate:" + paydate);
		System.out.println("skey:" + skey);
		System.out.println("error_code:" + errorCode);
		System.out.println("error_desc:" + errorDesc);
		System.out.println("channel:" + channel);

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
			if(skey.equals(key1)){
				log.info("SKey & Key1 Are Match>>>>");

				BigDecimal txAmount = new BigDecimal(amount);
				boolean validPayment = false;

				if (resDTO.getPyPayment() != null) {
					System.out.println("MolPayServlet| getPaymentId >>>>" + resDTO.getPyPayment().getPaymentId());
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
									System.out.println("Payment Status ID -" + smPayment.getPmStatus().getStatusId());
								}
							}else{
								System.out.println("smPayment.getPmStatus() is NULL");
							}
						}else{
							System.out.println("ord id or payment amount unmatch");
							System.out.println("ord id - " + ordId);
							log.info("paymentAmount id - " + paymentAmount);
						}
					}else if(status.equals(SupplierManagementConst.MOL_FAIL)){
						statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;

					}else if(status.equals(SupplierManagementConst.MOL_PENDING)){
						statusCode = SupplierManagementConst.PM_STAT_PENDING_PAYMENT;
					}

					Date txDate;
					try {
						txDate = DateUtils.convertStringToDate("yyyy-MM-dd HH:mm:ss", paydate);
					} catch (Exception e) {
						txDate = new Date();
						e.printStackTrace();
					}
					paymentOrder.setChannelName(channel);
					smPaymentRes.setTransactionId(tranID);
					smPaymentRes.setErrorCode(errorCode);
					smPaymentRes.setErrorDesc(errorDesc);
					smPaymentRes.setTransactionDate(txDate);
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
					
					//#26307 - Start
					if(cardType != null){
						if(cardType.equals(ParameterConst.CARD_TYPES_CREDIT)){
							PmParameter cards = ParameterHelper.getInstance().
									getParameterByTypeAndCode(ParameterTypeConst.CARD_TYPES, ParameterConst.CARD_TYPES_CREDIT);
							smPayment.setCardType(cards);
						}
						if(cardType.equals(ParameterConst.CARD_TYPES_DEBIT)){
							PmParameter cards = ParameterHelper.getInstance().
									getParameterByTypeAndCode(ParameterTypeConst.CARD_TYPES, ParameterConst.CARD_TYPES_DEBIT);
							smPayment.setCardType(cards);
						}
					}		
					//#26307 - End

					PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
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

					System.out.println(">>>>Before paymentBackendUpdate Upper<<<");
					String paymentType = StringUtils.EMPTY;
					if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE)){
						paymentType = SupplierManagementConst.MOF_PAYMENT;
						System.out.println(">>>>paymentType<<<"+paymentType);
					}else if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
						paymentType = SupplierManagementConst.PROCESSING_PAYMENT;
						System.out.println(">>>>paymentType<<<"+paymentType);
					}else if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)){
						paymentType = SupplierManagementConst.SOFTCERT_PAYMENT;
						System.out.println(">>>>paymentType<<<"+paymentType);
					}
					pymtReqDTO.setPaymentType(paymentType);
					pymtReqDTO.setMethodName("doPost-MolPayServlet");
					PaymentResDTO pymtResDTO = reqSO.paymentBackendUpdate(pymtReqDTO);
					System.out.println(">>>>Before paymentBackendUpdate<<<");
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
							System.out.println("Curl IPN - nbcb " + nbcb);
							try {
								SmCommonUtil.paymentCurlIPN(req, true, merchantId, molIpnUrl);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println("paymentCurlIPN error>>>>>>>>>" + e.getMessage());
							}
						}
					}
				}else{
					System.out.println("No SmPayment Object is found");
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
}
