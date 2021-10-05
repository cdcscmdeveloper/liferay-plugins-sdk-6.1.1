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
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.py.PyBillDtl;
import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.payment.PaymentResponseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;
//#26307 start 
import my.ep.constant.pm.ParameterConst; 
import my.ep.constant.pm.ParameterTypeConst; 
import my.ep.persistence.entity.pm.common.PmParameter; 
import my.ep.web.util.pm.helper.ParameterHelper; 
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
//#26307 start 

@ManagedBean(name = "paymentResponseBB")
@RequestScoped
public class PaymentResponseBackingBean extends BaseBackingBean<PaymentResponseManagedBean>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(PaymentResponseBackingBean.class);
	private final String molIpnUrl = EpPropertiesUtils.getInstance()
			.getProperty("molpay.ipnurl");

	public PaymentResponseBackingBean(){
		
	}
	
	@PostConstruct
	public void init() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		log.info("<<<<<<@PostConstruct>>>>>>>>");
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		HttpSession session = httpServletRequest.getSession();
		
		log.info("session - paymentBpmTaskId>>>>>>>>>" + session.getAttribute("paymentBpmTaskId"));
		log.info("session - paymentBpmPojo>>>>>>>>>" + session.getAttribute("paymentBpmPojo"));
		
		String paymentTaskId = (String)session.getAttribute("paymentBpmTaskId");
		Object paymentBpmPojo = (Object)session.getAttribute("paymentBpmPojo");
		SmAppl appl = (SmAppl)session.getAttribute("smAppl");
		
		session.removeAttribute("paymentBpmTaskId");
		session.removeAttribute("paymentBpmPojo");
		
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
		// #26307 start
		String cardType = null; 
		String extraP = httpServletRequest.getParameter("extraP");
		boolean fromManualPayment = false;
		//Start //26307
		boolean skipChecking = false;
		log.info("Enter Init");
		if(orderId == null){
			SmSupplier supp = SmApplicationUtil.getCurrentSupplier();			
			if(supp != null){
				log.info("supplier id =="+supp.getSupplierId());
				
				IPaymentSO reqSO1 = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
				Long paymentID = reqSO1.retrievePaymentId(supp.getSupplierId());
				PyPayment pyPymt = null;
				
				if(paymentID != null && paymentID > 0){
					log.info("paymentID =="+paymentID);
					IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
					PaymentReqDTO reqDTO = new PaymentReqDTO();
					reqDTO.setPaymentId(paymentID);
					PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
					pyPymt = resDTO.getPyPayment();
				}else{
					SmAppl activeAppl = SmCommonUtil.getActiveApplBySupplier(supp);
					if(activeAppl == null){
						log.info("activeAppl is null");
						SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
						supplierSearchReqDTO.setSupplierId(supp.getSupplierId());
						SupplierSearchResDTO supplierResDTO =  SmServiceLookup.getSupplierSearchSO().retriveLatestApplBySupplierId(supplierSearchReqDTO);
						activeAppl = supplierResDTO.getSmAppl();
						log.info("resDTO1.activeAppl() =="+activeAppl.getApplNo());
					}
					log.info("resDTO1.activeAppl() =="+activeAppl);
					if(activeAppl != null){
						log.info("active appl =="+activeAppl.getApplNo());
						PaymentReqDTO reqDTO = new PaymentReqDTO();
						//reqDTO.setApplId(this.getManagedBean().getAppId());
						reqDTO.setBillNo(activeAppl.getApplNo());
//						IPaymentSO reqSO1 = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
						PaymentResDTO resDTO1 = reqSO1.retrievePyBillByDocNo(reqDTO);
						log.info("resDTO1.getPyBill() =="+resDTO1.getPyBill());
						if(resDTO1.getPyBill() != null){
							log.info("resDTO1.getPyBill().getPyPayments() =="+resDTO1.getPyBill().getPyPayments().size());
							if(resDTO1.getPyBill().getPyPayments()!= null && !resDTO1.getPyBill().getPyPayments().isEmpty()){
								 pyPymt = resDTO1.getPyBill().getPyPayments().get(0);
								
							}
						}
					}	
				}	
				
				Long paymentId = pyPymt.getPaymentId();
				log.info("resDTO1.getPyBill().getPyPayments()  paymentId=="+ paymentId);
				orderId = paymentId.toString();
				errorDesc = "";
				errorCode = "";
				paydate = DateUtils.dateToString("yyyy-MM-dd HH:mm:ss", pyPymt.getPaymentDate());;
				if(pyPymt.getPyPaymentOrder() != null && pyPymt.getPyPaymentOrder().getPyPaymentResponses().size()>0){
					log.info("pyPymt.getPyPaymentOrder() channel name=="+ pyPymt.getPyPaymentOrder().getChannelName());
					channel = pyPymt.getPyPaymentOrder().getChannelName();
					tranID = pyPymt.getPyPaymentOrder().getPyPaymentResponses().get(0).getTransactionId();
					if(tranID != null && "123456".equalsIgnoreCase(tranID)){
						fromManualPayment = true;
					}
				}
				skipChecking = true;
				status = SupplierManagementConst.MOL_FAIL;
				if(pyPymt.getPaymentAmt() != null){
					amount = pyPymt.getPaymentAmt().toString();
				}	
				if(pyPymt.getPmStatus()!=null){	
					if(pyPymt.getPmStatus().getStatusId() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PAYMENT_SUCCESSFUL){
						status = SupplierManagementConst.MOL_SUCCESS;
					}else if(pyPymt.getPmStatus().getStatusId() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PENDING_PAYMENT_RESPONSE){
						status = SupplierManagementConst.MOL_PENDING;
					}else if(pyPymt.getPmStatus().getStatusId() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PAYMENT_FAILED){
						status = SupplierManagementConst.MOL_FAIL;
					}
				}	
			}
		}		
		//end //26307
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
		
		log.info("paymentTaskId>>>>>" + paymentTaskId);
		log.info("paymentBpmPojo>>>>>" + paymentBpmPojo);
		log.info("appl>>>>>>>>>>>" + appl);
		
		if(paymentTaskId != null && paymentBpmPojo != null &&  appl != null){
			responsePreRender(paymentTaskId, paymentBpmPojo, appl);
			responseRedirect();
		}else{
			log.info("<<<<<<<>PostConstruct Parameter - STart>>>>>>>>>>>");
			if(orderId != null && !fromManualPayment){//26307 to handle manual payment
				Long ordId = Long.valueOf(orderId);
				IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
				PaymentReqDTO reqDTO = new PaymentReqDTO();
				reqDTO.setPaymentId(ordId);
				PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);

				if(resDTO.getPyPayment() != null && ordId != null){
					String vKey = SmCommonUtil.getInstance().getPaymentGatewayVKeyByBillType(resDTO.getPyPayment().getPyBill().getBillType());
					// To verify the data integrity sending by MOLPay
					String key0 = SmCommonUtil.md5(tranID + orderId + status + domain + amount + currency);
					String key1 = SmCommonUtil.md5(paydate + domain + key0 + appcode + vKey);

					log.info("SKey>>>>" + skey);
					log.info("Key1>>>>" + key1);
					//26307
					if (key1.equals(skey) || skipChecking) {
						log.info("Enter 1");
						String billType = StringUtils.EMPTY;
						String personnelName = null;
						
						if(resDTO.getPyPayment() != null){
							billType = resDTO.getPyPayment().getPyBill().getBillType();
						}
						showPaymentResponse(status, resDTO.getPyPayment().getPaymentId(), errorDesc, billType, resDTO.getPyPayment().getPyBill().getPyBillDtls().get(0)) ;
					}else{
						showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES,null,null,null,null);
					}
				}else{
					showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES,null,null,null,null);
				}
				log.info("<<<<<<<>PostConstruct Parameter - End>>>>>>>>>>>");
				responseRedirect();
			}
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
	public void responsePreRender(String paymentTaskId, Object paymentBpmPojo, SmAppl appl) throws NoSuchMethodException,SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));


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
		// #26307 start
		String cardType = null; 
		String extraP = httpServletRequest.getParameter("extraP");
		
		//Start //26307
				boolean skipChecking = false;
				log.info("Enter Response Pre Render");
				
				if(orderId == null){
					SmSupplier supp = SmApplicationUtil.getCurrentSupplier();			
					if(supp != null){
						log.info("supplier id =="+supp.getSupplierId());
						
						IPaymentSO reqSO1 = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
						Long paymentID = reqSO1.retrievePaymentId(supp.getSupplierId());
						PyPayment pyPymt = null;
						
						if(paymentID != null && paymentID > 0){
							log.info("paymentID =="+paymentID);
							IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
							PaymentReqDTO reqDTO = new PaymentReqDTO();
							reqDTO.setPaymentId(paymentID);
							PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
							pyPymt = resDTO.getPyPayment();
						}else{
							SmAppl activeAppl = SmCommonUtil.getActiveApplBySupplier(supp);
							if(activeAppl == null){
								log.info("activeAppl is null");
								SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
								supplierSearchReqDTO.setSupplierId(supp.getSupplierId());
								SupplierSearchResDTO supplierResDTO =  SmServiceLookup.getSupplierSearchSO().retriveLatestApplBySupplierId(supplierSearchReqDTO);
								activeAppl = supplierResDTO.getSmAppl();
								log.info("resDTO1.activeAppl() =="+activeAppl.getApplNo());
							}
							log.info("resDTO1.activeAppl() =="+activeAppl);
							if(activeAppl != null){
								log.info("active appl =="+activeAppl.getApplNo());
								PaymentReqDTO reqDTO = new PaymentReqDTO();
								//reqDTO.setApplId(this.getManagedBean().getAppId());
								reqDTO.setBillNo(activeAppl.getApplNo());
//								IPaymentSO reqSO1 = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
								PaymentResDTO resDTO1 = reqSO1.retrievePyBillByDocNo(reqDTO);
								log.info("resDTO1.getPyBill() =="+resDTO1.getPyBill());
								if(resDTO1.getPyBill() != null){
									log.info("resDTO1.getPyBill().getPyPayments() =="+resDTO1.getPyBill().getPyPayments().size());
									if(resDTO1.getPyBill().getPyPayments()!= null && !resDTO1.getPyBill().getPyPayments().isEmpty()){
										 pyPymt = resDTO1.getPyBill().getPyPayments().get(0);
										
									}
								}
							}	
						}	
						
						Long paymentId = pyPymt.getPaymentId();
						log.info("resDTO1.getPyBill().getPyPayments()  paymentId=="+ paymentId);
						orderId = paymentId.toString();
						errorDesc = "";
						errorCode = "";
						paydate = DateUtils.dateToString("yyyy-MM-dd HH:mm:ss", pyPymt.getPaymentDate());;
						if(pyPymt.getPyPaymentOrder() != null && pyPymt.getPyPaymentOrder().getPyPaymentResponses().size()>0){
							log.info("pyPymt.getPyPaymentOrder() channel name=="+ pyPymt.getPyPaymentOrder().getChannelName());
							channel = pyPymt.getPyPaymentOrder().getChannelName();
							tranID = pyPymt.getPyPaymentOrder().getPyPaymentResponses().get(0).getTransactionId();
						}
						skipChecking = true;
						status = SupplierManagementConst.MOL_FAIL;
						if(pyPymt.getPaymentAmt() != null){
							amount = pyPymt.getPaymentAmt().toString();
						}	
						if(pyPymt.getPmStatus()!=null){	
							if(pyPymt.getPmStatus().getStatusId() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PAYMENT_SUCCESSFUL){
								status = SupplierManagementConst.MOL_SUCCESS;
							}else if(pyPymt.getPmStatus().getStatusId() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PENDING_PAYMENT_RESPONSE){
								status = SupplierManagementConst.MOL_PENDING;
							}else if(pyPymt.getPmStatus().getStatusId() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PAYMENT_FAILED){
								status = SupplierManagementConst.MOL_FAIL;
							}
						}	
					}
				}
				//end //26307
				
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
		if(extraP!=null){
			JSONObject obj; 
			try { 
				obj = new JSONObject(extraP); 
				cardType = obj.getString("cctype") != null ? obj.getString("cctype") : null; 
			} catch (JSONException e) { 
				e.printStackTrace(); 
			} 
		}
		
		// #26307 end

		Long ordId = null;
		if(orderId!= null){
			ordId = Long.valueOf(orderId);
		}		
		IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		reqDTO.setPaymentId(ordId);
		PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);

		if(resDTO.getPyPayment() != null && ordId != null){
			String vKey = SmCommonUtil.getInstance().getPaymentGatewayVKeyByBillType(resDTO.getPyPayment().getPyBill().getBillType());
			// To verify the data integrity sending by MOLPay
			String key0 = SmCommonUtil.md5(tranID + orderId + status + domain + amount + currency);
			String key1 = SmCommonUtil.md5(paydate + domain + key0 + appcode + vKey);

			log.info("SKey>>>>" + skey);
			log.info("Key1>>>>" + key1);
			//26307
			if (key1.equals(skey) || skipChecking) {
				//if(paymentTaskId != null && paymentBpmPojo != null){
				log.info("SKey & Key1 Are Match>>>>");

				BigDecimal txAmount = new BigDecimal(amount);
				//Long txID = Long.valueOf(tranID);


				boolean validPayment = false;
/*
				session.removeAttribute("paymentBpmTaskId");
				session.removeAttribute("paymentBpmPojo");*/

				PyPayment pyPayment = resDTO.getPyPayment();
				PyPaymentResponse smPaymentRes = new PyPaymentResponse();
				PyPaymentOrder paymentOrder = pyPayment.getPyPaymentOrder();
				Long statusCode = null;
				Long paymentId = pyPayment.getPaymentId();

				log.info("Status Success 0>>>>>");
				String softcertUserName = null;
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
				// #26307 start 
				PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
				if (cardType != null) { 
					if (cardType.equalsIgnoreCase("Credit")) {
						cardType = "CC";
					} else if (cardType.equalsIgnoreCase("Debit")) {
						cardType = "DC";
					} 
					ParameterReqDTO reqPmDTO = new ParameterReqDTO();
					log.info("cardType===="+cardType);
					if (cardType.equals(ParameterConst.CARD_TYPES_CREDIT)) { 
						log.info("cardType====enter CC"+cardType);
						reqPmDTO.setParameterType(ParameterTypeConst.CARD_TYPES);
						reqPmDTO.setParameterCode(ParameterConst.CARD_TYPES_CREDIT);
						ParameterResDTO resPmDTO = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqPmDTO);
						log.info("cardType====enter CC result "+resPmDTO.getPmParameter());
						PmParameter cards = resPmDTO.getPmParameter();
						pymtReqDTO.setCardType(cards);
						pymtReqDTO.setCardTypeS(cardType);
						pyPayment.setCardType(cards);	
						channel = "credit";
					} else if (cardType.equals(ParameterConst.CARD_TYPES_DEBIT)) {
						log.info("cardType====enter DC"+cardType);
						reqPmDTO.setParameterType(ParameterTypeConst.CARD_TYPES);
						reqPmDTO.setParameterCode(ParameterConst.CARD_TYPES_DEBIT);
						ParameterResDTO resPmDTO = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqPmDTO);
						log.info("cardType====enter CC result "+resPmDTO.getPmParameter());
						PmParameter cards = resPmDTO.getPmParameter();
						pymtReqDTO.setCardType(cards);
						pymtReqDTO.setCardTypeS(cardType);
						pyPayment.setCardType(cards);	
						channel = "debit";
					} 
				} 
				
				//START THIS LINE OF CODE DO NOT MERGE TO PRODUCTION
				if(cardType == null && channel != null && !"credit".equalsIgnoreCase(channel) && !"debit".equalsIgnoreCase(channel)){
					channel = "FPX";
				}
				//END THIS LINE OF CODE DO NOT MERGE TO PRODUCTION
				
				// #26307 end 

				Date txDate;
				try {
					txDate = DateUtils.convertStringToDate(
							"yyyy-MM-dd HH:mm:ss", paydate);
				} catch (Exception e) {
					txDate = new Date();
					e.printStackTrace();
				}
				log.info("channel--------------------26307"+channel);
				paymentOrder.setChannelName(channel);
				smPaymentRes.setTransactionId(tranID);
				smPaymentRes.setErrorCode(errorCode);
				smPaymentRes.setErrorDesc(errorDesc);
				smPaymentRes.setTransactionDate(txDate);
				smPaymentRes.setPyPaymentOrder(paymentOrder);
				smPaymentRes.setPaymentStatus(status);
				smPaymentRes.setPaidAmt(txAmount);
				paymentOrder.getPyPaymentResponses().add(smPaymentRes);
				//26307
				log.info("Status Code : "+statusCode);
				if(statusCode!= null){
					PmStatus paymentStatus = SmCommonUtil.getInstance().retrievePmStatusById(statusCode);
					pyPayment.setPmStatus(paymentStatus);
				}
				

				// updatePayment(smPayment,validPayment);
				Object token = getFacesScopes().getBPMAuthenticationCtxToken();
				

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
				pymtReqDTO.setMethodName("responsePreRender-PaymentResponseBackingBean");
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
						String merchantId = SmCommonUtil.getInstance().getPaymentGatewayMerchantIdByBillType(pyPayment.getPyBill().getBillType());
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
			showPaymentResponse(status,Long.valueOf(orderId),errorDesc,billType,resDTO.getPyPayment().getPyBill().getPyBillDtls().get(0));
		}else{
			showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES,null,null,null,null);
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
	public void showPaymentResponse(String status, Long paymentId, String errorDesc, String billType, PyBillDtl billDtl) {
		log.info(">>>>showPaymentResponse<<<<<<<<<<" + status);
		
		boolean paymentStatus = false;
		switch (status) {
		case SupplierManagementConst.MOL_SUCCESS:
			log.info(">>>>MOL_SUCCESS<<<<<<<<<<" + status);
			String msgCode = null;

			if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
				msgCode = MessageConst.INFO_721058;
			}else if(billType.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)){
				msgCode = MessageConst.INFO_721065;
				
				getManagedBean().setMsgParam(getSoftCertNameEmail(billDtl));
			}else{
				msgCode = MessageConst.INFO_721064;
			}
			
			PaymentAcknowledgeBackingBean pymtAckBB = new PaymentAcknowledgeBackingBean();
			pymtAckBB.getManagedBean().setPaymentId(paymentId);
			
			getManagedBean().setMessageId(msgCode);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_INFO);
			paymentStatus = true;
			break;

		case SupplierManagementConst.MOL_FAIL:
			log.info(">>>>MOL_FAIL<<<<<<<<<<" + status);
			getManagedBean().setMessageId(MessageConst.ERROR_721051);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_ERROR);
			getManagedBean().setErrDesc(getPaymentMsgLabel(billType,true));
			break;

		case SupplierManagementConst.MOL_PENDING:
			log.info(">>>>MOL_PENDING<<<<<<<<<<" + status);
			getManagedBean().setMessageId(MessageConst.INFO_721055);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_INFO);
			break;

		case SupplierManagementConst.PAYMENT_INVALID_RES:
			log.info(">>>>PAYMENT_INVALID_RES<<<<<<<<<<" + status);
			getManagedBean().setMessageId(MessageConst.EP_MAJ_00001);
			getManagedBean().setMessageSeverity(FacesMessage.SEVERITY_ERROR);
			break;

		}

		
		
		if (paymentStatus && paymentId != null) {
			getManagedBean().setPaymentId(paymentId);
			getManagedBean().setPaymentStatus(true);
		}else{
			getManagedBean().setPaymentId(null);
			getManagedBean().setPaymentStatus(false);
		}
		
		log.info("Severity >>>>>>>>" + getManagedBean().getMessageSeverity().toString());
		log.info("Message Id >>>>>>>>" + getManagedBean().getMessageId().toString());
		log.info("Payment Id >>>>>>>>" + getManagedBean().getPaymentId());
		log.info("Payment Status >>>>>>>>" + getManagedBean().isPaymentStatus());

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

	public void getData(){
		
	}

	public void responseRedirect(){
		log.info("*************responseRedirect*********");
		FacesContext fc = FacesContext.getCurrentInstance();

		String errorDesc = getManagedBean().getErrDesc() != null ? getManagedBean().getErrDesc() : "";
		if(fc.getMessageList().size() < 1){
			String[] msgParam = null;
			
			if(getManagedBean().getMessageId().equalsIgnoreCase(MessageConst.INFO_721065)){
				msgParam = getManagedBean().getMsgParam();
			}else{
				msgParam = new String[]{errorDesc};
			}
			fc.addMessage(
					null,
					new FacesMessage(getManagedBean().getMessageSeverity(), null,
							MessageUtils.getMessage(getManagedBean().getMessageId(),msgParam,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		
		}

		fc.getApplication()
		.getNavigationHandler()
		.handleNavigation(fc, null,
				"/xhtml/sm/payment/paymentAcknowledge.xhtml");
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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
	
	private String[] getSoftCertNameEmail(PyBillDtl bill){
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setPersonnelId(bill.getBillDtlRefId());
		SmPersonnel personnel = SmServiceLookup.getPersonnelSO().retrievePersonnelById(reqDTO).getSmPersonnel();

		if(personnel != null){
			String[] strArr = new String[]{personnel.getName(), personnel.getEmail()};
			return strArr;
		}

		return null;
	}
	
/*	public static void main(String args[]){
		String tranID = "355374";
		String orderId = "215";
		String status = "00";
		String domain = "cdc4_Dev";
		String amount = "50.00";
		String currency = "RM";
		String appcode = "657300";
		String paydate = "2017-09-15 16:07:05";
		String vKey = "11504645159a52c481f04ded493c8c13";
		String sKey = "6a53011c1afd949ca399a7a4547d96db";
		
		String tranID = "356409";
		String orderId = "224";
		String status = "00";
		String domain = "cdc4_Dev";
		String amount = "50.00";
		String currency = "RM";
		String appcode = "657300";
		String paydate = "2017-09-19 17:44:35";
		String vKey = "11504645159a52c481f04ded493c8c13";
		String sKey = "8443165a504774d4e4a8f14e0ca0e49f";
		
		String key0 = SmCommonUtil.md5(tranID + orderId + status + domain + amount + currency);
		String key1 = SmCommonUtil.md5(paydate + domain + key0 + appcode + vKey);
		
		System.out.println("skey >>>>" + sKey);
		System.out.println("key1>>>>>" + key1);
	}*/
	
	
}
