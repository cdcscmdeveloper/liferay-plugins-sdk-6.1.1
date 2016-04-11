package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Servlet implementation class MolPayServlet
 */
//@WebServlet("/MolPayServlet")
public class MolPayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(MolPayServlet.class);
	private final String vKey = EpPropertiesUtils.getInstance().getProperty("molpay.vkey");
	private final String merchantId = EpPropertiesUtils.getInstance().getProperty("molpay.merchantid");
	private final String molIpnUrl = EpPropertiesUtils.getInstance().getProperty("molpay.ipnurl");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("******Servlet Testing doGet*********");
		PrintWriter out = res.getWriter();
		out.print("CBTOKEN:MPSTATOK");

		//res.sendRedirect("http://localhost:8080/web/guest/svo-site-visit");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("************Payment PostBack Start********");
		log.info("******Servlet Testing doPost*********");

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

		if(nbcb != null){
			//To verify the data integrity sending by MOLPay
			String key0 = SmCommonUtil.md5(tranID + orderId + status + domain + amount + currency);
			String key1 = SmCommonUtil.md5(paydate + domain + key0 + appcode + vKey);

			log.info("SKey>>>>" + skey);
			log.info("Key1>>>>" + key1);
			if(skey.equals(key1)){
				log.info("SKey & Key1 Are Match>>>>");
				IPaymentSO reqSO = SmServiceLookup.getPaymentSO();
				PaymentReqDTO reqDTO = new PaymentReqDTO();


				log.info("SKey & Key1 Are Match>>>>");
				Long ordId = Long.valueOf(orderId);
				reqDTO.setPaymentId(ordId);
				PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
				BigDecimal txAmount = new BigDecimal(amount);
				boolean validPayment = false;

				if (resDTO.getPyPayment() != null) {
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

					Date txDate;
					try {
						txDate = DateUtils.convertStringToDate("yyyy-MM-dd HH:mm:ss", paydate);
					} catch (Exception e) {
						txDate = new Date();
						e.printStackTrace();
					}
					paymentOrder.setChannelName(channel);
					smPaymentRes.setTransactionId(Long.valueOf(tranID));
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

					log.info(">>>>Before paymentBackendUpdate<<<");
					String paymentType = StringUtils.EMPTY;
					if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE)){
						paymentType = SupplierManagementConst.MOF_PAYMENT;
					}else if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
						paymentType = SupplierManagementConst.PROCESSING_PAYMENT;
					}else if(smPayment.getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)){
						paymentType = SupplierManagementConst.SOFTCERT_PAYMENT;
					}
					pymtReqDTO.setPaymentType(paymentType);
					PaymentResDTO pymtResDTO = reqSO.paymentBackendUpdate(pymtReqDTO);
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
							out.print("CBTOKEN:MPSTATOK");
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
}
