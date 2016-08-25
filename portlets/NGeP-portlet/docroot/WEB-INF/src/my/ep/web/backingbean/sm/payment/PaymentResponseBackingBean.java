package my.ep.web.backingbean.sm.payment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.payment.PaymentResponseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "paymentResponseBB")
@RequestScoped
public class PaymentResponseBackingBean extends BaseBackingBean<PaymentResponseManagedBean>{
	public static Logger log = Logger.getLogger(PaymentResponseBackingBean.class);
	private final String merchantId = EpPropertiesUtils.getInstance()
			.getProperty("molpay.merchantid");
	private final String vKey = EpPropertiesUtils.getInstance().getProperty(
			"molpay.vkey");

	private final String molIpnUrl = EpPropertiesUtils.getInstance()
			.getProperty("molpay.ipnurl");
	
	public PaymentResponseBackingBean(){
		
	}
	
	@PostConstruct
	public void init() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		HttpSession session = httpServletRequest.getSession();
		
		String paymentTaskId = (String)session.getAttribute("paymentBpmTaskId");
		Object paymentBpmPojo = (Object)session.getAttribute("paymentBpmPojo");
		
		if(paymentTaskId != null && paymentBpmPojo != null){
			responsePreRender();
		}
		
	}
	
	/**
	 * Payment gateway processing and response status
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void responsePreRender() throws NoSuchMethodException,SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		HttpSession session = httpServletRequest.getSession();

		SmAppl appl = null;
		String paymentTaskId = null;
		Object paymentBpmPojo = null;

		paymentTaskId = (String)session.getAttribute("paymentBpmTaskId");
		paymentBpmPojo = (Object)session.getAttribute("paymentBpmPojo");
		appl = (SmAppl)session.getAttribute("smAppl");
	
		log.info("paymentTaskId>>>>>>>" + paymentTaskId);
		log.info("paymentBpmPojo>>>>>>>" + paymentBpmPojo);

		String orderId = httpServletRequest.getParameter("orderid");
		String tranID = httpServletRequest.getParameter("tranID");
		String status = httpServletRequest.getParameter("status");
		String domain = httpServletRequest.getParameter("domain");
		String amount = httpServletRequest.getParameter("amount");
		String currency = httpServletRequest.getParameter("currency");
		String appcode = httpServletRequest.getParameter("appcode");
		String paydate = httpServletRequest.getParameter("paydate");
		String skey = httpServletRequest.getParameter("skey");
		String channel = httpServletRequest.getParameter("channel") != null ? httpServletRequest.getParameter("channel") : null;
		String errorCode = httpServletRequest.getParameter("error_code") != null ? httpServletRequest.getParameter("error_code") : null;
		String errorDesc = httpServletRequest.getParameter("error_desc") != null ? httpServletRequest.getParameter("error_desc") : null;


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

		// To verify the data integrity sending by MOLPay
		String key0 = SmCommonUtil.md5(tranID + orderId + status + domain
				+ amount + currency);
		String key1 = SmCommonUtil
				.md5(paydate + domain + key0 + appcode + vKey);

		log.info("SKey>>>>" + skey);
		log.info("Key1>>>>" + key1);
		if (skey.equals(key1)) {
			//if(paymentTaskId != null && paymentBpmPojo != null){
			log.info("SKey & Key1 Are Match>>>>");
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IPaymentSO.class);
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			Long ordId = Long.valueOf(orderId);
			BigDecimal txAmount = new BigDecimal(amount);
			Long txID = Long.valueOf(tranID);
			reqDTO.setPaymentId(ordId);
			PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
			boolean validPayment = false;

			if (resDTO.getPyPayment() != null) {

				session.removeAttribute("paymentBpmTaskId");
				session.removeAttribute("paymentBpmPojo");

				PyPayment pyPayment = resDTO.getPyPayment();

				PyPaymentResponse smPaymentRes = new PyPaymentResponse();
				PyPaymentOrder paymentOrder = pyPayment.getPyPaymentOrder();
				Long statusCode = null;
				Long paymentId = pyPayment.getPaymentId();

				log.info("Status Success 0>>>>>");
				if (status.equals(SupplierManagementConst.MOL_SUCCESS)) {
					BigDecimal paymentAmount = pyPayment.getPaymentAmt();
					log.info("paymentId>>>>" + paymentId);
					log.info("orderId>>>>" + orderId);
					log.info("paymentAmount>>>>" + paymentAmount);
					log.info("txAmount>>>>" + txAmount);

					if (paymentId.longValue() == ordId.longValue()
							&& paymentAmount.compareTo(txAmount) == 0) {
						log.info("current status>>>>"
								+ pyPayment.getPmStatus().getStatusId());
						if (pyPayment.getPmStatus() != null) {
							if (pyPayment.getPmStatus().getStatusId() == SupplierManagementConst.PM_STAT_PENDING_PAYMENT
									|| pyPayment.getPmStatus().getStatusId() == SupplierManagementConst.PM_STAT_FAILED_PAYMENT) {
								log.info("current payment>>>>"
										+ pyPayment.getPmStatus().getStatusId());

								if (!pyPayment
										.getPmStatus()
										.getStatusId()
										.equals(SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT)) {
									statusCode = SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT;
									validPayment = true;
								} else {
									statusCode = pyPayment.getPmStatus()
											.getStatusId();
								}
							}
						} else {
							log.info("<<<existing status is NUll>>>>");
							statusCode = SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT;
							validPayment = true;
						}
					} else {
						statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;
					}
				} else if (status.equals(SupplierManagementConst.MOL_PENDING)) {
					statusCode = SupplierManagementConst.PM_STAT_PENDING_PAYMENT;

				} else if (status.equals(SupplierManagementConst.MOL_FAIL)) {
					statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;
				}

				Date txDate;
				try {
					txDate = DateUtils.convertStringToDate(
							"yyyy-MM-dd HH:mm:ss", paydate);
				} catch (Exception e) {
					txDate = new Date();
					e.printStackTrace();
				}
				paymentOrder.setChannelName(channel);
				smPaymentRes.setTransactionId(txID);
				smPaymentRes.setErrorCode(errorCode);
				smPaymentRes.setErrorDesc(errorDesc);
				smPaymentRes.setTransactionDate(txDate);
				smPaymentRes.setPyPaymentOrder(paymentOrder);
				smPaymentRes.setPaymentStatus(status);
				smPaymentRes.setPaidAmt(txAmount);
				paymentOrder.getPyPaymentResponses().add(smPaymentRes);

				PmStatus paymentStatus = SmCommonUtil.getInstance().retrievePmStatusById(statusCode);
				pyPayment.setPmStatus(paymentStatus);

				// updatePayment(smPayment,validPayment);
				Object token = getFacesScopes().getBPMAuthenticationCtxToken();
				PaymentReqDTO pymtReqDTO = new PaymentReqDTO();

				//pymtReqDTO.setPaidPersonnels(getPaidPersonnelBeforeProceed());

				pymtReqDTO.setValidPayment(validPayment);
				pymtReqDTO.setPyPayment(pyPayment);
				pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);

				log.info("MB TaskId>>>>" + paymentTaskId);
				log.info("MB BpmPojo>>>>" + paymentBpmPojo);
				pymtReqDTO.setTaskId(paymentTaskId);
				pymtReqDTO.setBpmPojo(paymentBpmPojo);
				pymtReqDTO.setToken(token);

				log.info("pymtReqDTO TaskId>>>>" + pymtReqDTO.getTaskId());
				log.info("pymtReqDTO BpmPojo>>>>" + pymtReqDTO.getBpmPojo());
				FacesScopes fs = new FacesScopes();
				pymtReqDTO.setServerName(fs.getRequest().getServerName());
				pymtReqDTO.setServerPort(fs.getRequest().getServerPort());
				pymtReqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
				pymtReqDTO.setBackEndUpdate(false);
				pymtReqDTO.setRespondFromPaymentGateway(true);

				String paymentType = StringUtils.EMPTY;
				paymentType = getPaymentFeeTypeByBillType(pyPayment.getPyBill().getBillType());
				pymtReqDTO.setPaymentType(paymentType);
				PaymentResDTO pymtResDTO = null;

				log.info(">>>>>counter equal to 1<<<<<<");
				pymtResDTO = reqSO.updatePayment(pymtReqDTO);

				log.info("******ResponsePreRender - After Update******");
				if(pymtResDTO != null && pymtResDTO.isSuccessful()){
					//to auto generate mof cert
					/*if(pyPayment.getPyBill().getBillType().
								equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE) && validPayment){
							try{
								SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(pyPayment.getPyBill().getBillRefId());
							}catch(Exception ex){
								log.info("<<<<Payment - error when generate Virtual Cert>>>>" + ex.getMessage());
							}
						}*/
					try {
						SmCommonUtil.paymentCurlIPN(httpServletRequest,true,merchantId,molIpnUrl);
					} catch (Exception ex) {
						log.error("curlIPN: " + ex.getMessage());
					}
				}
			} else {
				status = SupplierManagementConst.PAYMENT_INVALID_RES;
			}
			
			String billType = StringUtils.EMPTY;
			if(resDTO.getPyPayment() != null){
				billType = resDTO.getPyPayment().getPyBill().getBillType();
			}
			showPaymentResponse(status,Long.valueOf(orderId),errorDesc,billType);

		} else {
			showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES,null,null,null);
		}
	}
	
	/**
	 * Get payment gateway response status
	 * 
	 * @param String
	 *            status
	 * @param Long
	 *            paymentId
	 */
	public void showPaymentResponse(String status, Long paymentId, String errorDesc, String billType) {
		boolean paymentStatus = false;
		switch (status) {
		case SupplierManagementConst.MOL_SUCCESS:
			String msgCode = null;
			
				if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
					msgCode = MessageConst.INFO_721058;
				}else if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)){
					msgCode = MessageConst.INFO_721065;
				}else{
					msgCode = MessageConst.INFO_721064;
				}
			
			getManagedBean().setMessageId(msgCode);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_INFO);
			paymentStatus = true;
			break;

		case SupplierManagementConst.MOL_FAIL:
			getManagedBean().setMessageId(MessageConst.ERROR_721051);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_ERROR);
			getManagedBean().setErrDesc(errorDesc);
			
			break;

		case SupplierManagementConst.MOL_PENDING:
			getManagedBean().setMessageId(MessageConst.INFO_721055);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_INFO);
			break;

		case SupplierManagementConst.PAYMENT_INVALID_RES:
			getManagedBean().setMessageId(MessageConst.EP_MAJ_00001);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_ERROR);
			break;

		}
		
		if (paymentStatus && paymentId != null) {
			//PaymentAcknowledgeBackingBean paymentAckBB = new PaymentAcknowledgeBackingBean();
			getManagedBean().setPaymentId(paymentId);
			getManagedBean().setPaymentStatus(true);
			//paymentAckBB.getManagedBean().setPaymentMessage(fsMsg);
		}else{
			getManagedBean().setPaymentId(null);
			getManagedBean().setPaymentStatus(false);
			//paymentAckBB.getManagedBean().setPaymentMessage(fsMsg);
		}
		
		
		getManagedBean().setBillTypeLbl(getPaymentMsgLabel(billType,false));
	}
	
	/**
	 * get Payment Msg Label
	 * @param paymentType
	 * @return String
	 */
	private String getPaymentMsgLabel(String billType, boolean isMessage){
		String pType = null;
		
		if(billType != null){
			if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
				pType = "lbl.sm.payment.response.type.proccessing";
			}else if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE)){
				pType = "lbl.sm.payment.response.type.registration";
			}else if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)){
				pType = "lbl.sm.payment.response.type.softcert";
			}
		}
		
		if(isMessage){
			return SmCommonUtil.getMessage(pType, null);
		}
		
		return pType;
	}
	
	/**
	 * get Payment Fee Type
	 * @param paymentType
	 * @return String
	 */
	private String getPaymentFeeTypeByBillType(String billType){
		String feeType = null;
		
		if (billType.equalsIgnoreCase(
						my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE)) {
			feeType = SupplierManagementConst.MOF_PAYMENT;
		} else if (billType.equalsIgnoreCase(
						my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)) {
			feeType = SupplierManagementConst.PROCESSING_PAYMENT;
		} else if (billType.equalsIgnoreCase(
						my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)) {
			feeType = SupplierManagementConst.SOFTCERT_PAYMENT;
		}
		
		return feeType;
	}
	
	public String getData(){
		return "";
	}
	
	public void responseRedirect(){
		log.info("*************responseRedirect*********");
		
		//this.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		
		log.info("Severity >>>>>>>>" + getManagedBean().getMessageSeverity().toString());
		log.info("Message Id >>>>>>>>" + getManagedBean().getMessageId().toString());
		log.info("Payment Id >>>>>>>>" + getManagedBean().getPaymentId());
		log.info("Payment Status >>>>>>>>" + getManagedBean().isPaymentStatus());
		
		String errorDesc = getManagedBean().getErrDesc() != null ? getManagedBean().getErrDesc() : "";
		fc.addMessage(
				null,
				new FacesMessage(getManagedBean().getMessageSeverity(), null,
						MessageUtils.getMessage(getManagedBean().getMessageId(),new String[]{errorDesc},
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		
		fc.getApplication()
		.getNavigationHandler()
		.handleNavigation(fc, null,
				"/xhtml/sm/payment/paymentAcknowledge.xhtml");
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void printReceipt() throws IOException{
		log.info("printReceipt>>>>" + this.getManagedBean().getPaymentId());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_PAYMENT_ID, this.getManagedBean().getPaymentId() + "");
		
		generateReport(ReportConst.SM_PAYMENT_RECEIPT_JSP, paramMap);
	}
}
