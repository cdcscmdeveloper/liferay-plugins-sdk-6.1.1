package my.ep.web.backingbean.sm.payment;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.py.PyBill;
import my.ep.persistence.entity.py.PyBillDtl;
import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.py.PyPaymentChargeLine;
import my.ep.persistence.entity.py.PyPaymentDtl;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.payment.PaymentManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "paymentBackingBean")
@RequestScoped
public class PaymentBackingBean extends BaseBackingBean<PaymentManagedBean> {

	public static Logger log = Logger.getLogger(PaymentBackingBean.class);
	private final String merchantId = EpPropertiesUtils.getInstance()
			.getProperty("molpay.merchantid");
	private final String vKey = EpPropertiesUtils.getInstance().getProperty(
			"molpay.vkey");
	private final String paymentGatewayUrl = EpPropertiesUtils.getInstance()
			.getProperty("molpay.url") + merchantId + "/";
	private final String molIpnUrl = EpPropertiesUtils.getInstance()
			.getProperty("molpay.ipnurl");
	private String vCode;
	private final String paymentGatewayOnOff = EpPropertiesUtils.getInstance().getProperty("paymentgateway.molpay.onoff");

	private Map<String, PmParameterDesc> supplierPaymentItemMap;

	/*@ManagedProperty(value = "#{paymentBean}")
	private PaymentBean paymentBean;*/

	/**
	 * Construct and initialized PaymentBackingBean
	 */
	public PaymentBackingBean() {
		this.getManagedBean().setCountry("MY");
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * @return the vKey
	 */
	public String getvKey() {
		return vKey;
	}

	/**
	 * @return the paymentGatewayUrl
	 */
	public String getPaymentGatewayUrl() {
		return paymentGatewayUrl;
	}

	/**
	 * @return the vCode
	 */
	public String getvCode() {
		return vCode;
	}

	/**
	 * @param vCode
	 *            the vCode to set
	 */
	public void setvCode(String vCode) {
		this.vCode = vCode;
	}
	
	/**
	 * @return the paymentGatewayOnOff
	 */
	public String getPaymentGatewayOnOff() {
		if(paymentGatewayOnOff != null){
			return paymentGatewayOnOff.trim();
		}else{
			return null;
		}
	}

	/**
	 * Set payment item description
	 */
	public void retrievePaymentItem() {
		List<PmParameterDesc> pmParameterDesc = SmCommonUtil
				.retrieveParameterDescList(
						SupplierManagementConst.PM_PAYMENT_ITEM,
						new FacesScopes().getUserLocale().getLanguage());
		supplierPaymentItemMap = new HashMap<String, PmParameterDesc>();

		for (PmParameterDesc pmDesc : pmParameterDesc) {
			supplierPaymentItemMap.put(pmDesc.getPmParameter()
					.getParameterCode(), pmDesc);
		}
	}

	public void confirmPayment() {

	}

	/**
	 * Retrieve bill by application id from task list
	 */
	public void retrieveBillInfo() {
		/*
		 * for testing Purpose
		 */
		log.debug(">>>>>>>InitPayment Stert>>>>");
		/*
		 * MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
		 * mofReqDTO.setApplId(1009); IMofRegistrationSO mofRegistrationSO =
		 * (IMofRegistrationSO)
		 * BusinessDelegate.getInstance().getServiceObject("sm",
		 * IMofRegistrationSO.class); MofRegistrationResDTO mofResDTO =
		 * mofRegistrationSO.retrieveSupplierProfileByApplId(mofReqDTO);
		 * this.getManagedBean().setSmAppl(mofResDTO.getSmAppl());
		 * this.getManagedBean().setAppId(mofReqDTO.getApplId());
		 */// dummy test

		// this.getManagedBean().setAppId(2997L);
		log.info("*******retrieveBillInfo Start******" + getManagedBean().getDocNo());
		IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IPaymentSO.class);
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		//reqDTO.setApplId(this.getManagedBean().getAppId());
		reqDTO.setBillNo(getManagedBean().getDocNo());
		PaymentResDTO resDTO = reqSO.retrievePyBillByDocNo(reqDTO);
		this.getManagedBean().setSmAppl(retriveSmApplById(resDTO.getPyBill().getBillRefId()));

		log.info("*******Bill Object**********"
				+ resDTO.getPyBill().getBillId());
		this.getManagedBean().setPyBill(resDTO.getPyBill());
		Long billId = resDTO.getPyBill().getBillId();
		List<PyBillDtl> smBillDtls = resDTO.getPyBill().getPyBillDtls();

		if (resDTO.getPyBill().getPyPayments().size() > 0) {
			// System.out.println("******check is it Offline Payment*****");
			/*
			 * if(checkOfflinePayment(billId)){
			 * //this.getManagedBean().setOfflinePayment(true);
			 * System.out.println("*****is Offline Payment*****"); BigDecimal
			 * ttlAmount = new BigDecimal(0);
			 * 
			 * this.getManagedBean().setNewSmBillDtlLs(new
			 * ArrayList<SmBillDtl>()); for(SmBillDtl billDtl : smBillDtls){
			 * ttlAmount = ttlAmount.add(billDtl.getItemPrice());
			 * this.getManagedBean().getNewSmBillDtlLs().add(billDtl); }
			 * 
			 * System.out.println("TotalAmount>>>>>>" + ttlAmount);
			 * this.getManagedBean().setTotalAmount(ttlAmount);
			 * 
			 * FacesContext fc = FacesContext.getCurrentInstance();
			 * fc.getApplication().getNavigationHandler().handleNavigation(fc,
			 * null,"/xhtml/sm/payment/paymentSummary.xhtml"); }else{
			 */
			// this.getManagedBean().setOfflinePayment(false);
			log.debug("*****is Online Payment*****");

			proceedToItemList(smBillDtls);
			// }
		} else {
			proceedToItemList(smBillDtls);
		}
	}

	/**
	 * Set item list based on bill type and redirect to payment page.
	 * ie:Softcert payment, processing fee and registration fee
	 * 
	 * @param List
	 *            <SmBillDtl> smBillDtlLs
	 */
	public void proceedToItemList(List<PyBillDtl> pyBillDtlLs) {
		getManagedBean().setSoftcertPayment(false);
		this.getManagedBean().setDefaultCertPaymentMap(
				new HashMap<Long, PyBillDtl>());
		for (PyBillDtl billDtl : pyBillDtlLs) {
			if (billDtl.getItemCode().getParameterCode()
					.equals(SupplierManagementConst.SOFTCERT_PAYMENT)) {
				getManagedBean().setSoftcertPayment(true);
				//TODO: need to fix && new payment design
				this.getManagedBean()
						.getDefaultCertPaymentMap()
						.put(billDtl.getBillDtlRefId(), billDtl);
						
			} else {
				this.getManagedBean().setApplicationPayment(billDtl);
			}
		}

		if (getManagedBean().isSoftcertPayment()) {
			setBilledSoftCertPersonnels(pyBillDtlLs);
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/sm/payment/portletViewMode.xhtml");
	}

	/**
	 * Set selected item to be paid and redirect to payment summary page
	 * 
	 * @return String
	 */
	public String proceedToPaymentSummary() {
		log.debug("****proceedToPaymentSummary****");

		PyBill pyBill = this.getManagedBean().getPyBill();
		BigDecimal ttlAmount = new BigDecimal(0);
		this.getManagedBean().setNewPyBillDtlLs(new ArrayList<PyBillDtl>());
		retrievePaymentItem();

		Map<Long, SmPersonnel> selectedSoftCertMap = new HashMap<Long, SmPersonnel>();
		if (this.getManagedBean().getSelectedSoftCertLs() != null
				&& this.getManagedBean().getSelectedSoftCertLs().length > 0) {
			for (SmPersonnel selectedPersonnel : this.getManagedBean()
					.getSelectedSoftCertLs()) {
				selectedSoftCertMap.put(selectedPersonnel.getPersonnelId(),
						selectedPersonnel);
			}
		}

		for (PyBillDtl billDtl : pyBill.getPyBillDtls()) {
			if (billDtl.getItemCode().getParameterCode()
					.equals(SupplierManagementConst.SOFTCERT_PAYMENT)) {
				//TODO : need to fix && new payment design
				if (selectedSoftCertMap != null
						&& !selectedSoftCertMap.isEmpty()
						&& selectedSoftCertMap.containsKey(billDtl.getBillDtlRefId())) {
					this.getManagedBean().getNewPyBillDtlLs().add(billDtl);
				}
			} else {
				this.getManagedBean().getNewPyBillDtlLs().add(billDtl);
			}
		}

		for (PyBillDtl newItemDtlLs : this.getManagedBean().getNewPyBillDtlLs()) {
			ttlAmount = ttlAmount.add(newItemDtlLs.getItemPrice());
			log.info("TotalAmount>>>>>>" + ttlAmount);
		}
		getManagedBean().setTotalAmountBeforeGst(
				ttlAmount.setScale(2, BigDecimal.ROUND_UP));
		BigDecimal gst = getManagedBean().isSoftcertPayment() ? SmCommonUtil
				.getGstCurrentRate().divide(new BigDecimal(100))
				.multiply(ttlAmount) : BigDecimal.ZERO;
		getManagedBean().setGstAmount(gst.setScale(2, BigDecimal.ROUND_UP));
		this.getManagedBean().setTotalAmount(
				gst.add(ttlAmount).setScale(2, BigDecimal.ROUND_UP));
		return "paymentSummary";
	}

	/**
	 * Create and update payment,bill,personnel and call payment gateway
	 * 
	 * @return String
	 */
	public String submitPayment() {
		log.debug("********submitPayment******");
		if (this.getManagedBean().getPyBill() != null) {
			List<PmParameterDesc> paymentModeLs = SmCommonUtil
					.retrieveParameterDescList(
							SupplierManagementConst.PM_PAYMENT_MODE,
							new FacesScopes().getUserLocale().getLanguage());
			List<PmStatusDesc> paymentStatusLs = SmCommonUtil
					.retrievePmStatusList("SM",
							SupplierManagementConst.PM_STATUS_PAYMENT,
							new FacesScopes().getUserLocale().getLanguage());
			PmParameter paymentMode = new PmParameter();
			PyPayment payment = new PyPayment();
			PmStatus paymentStatus = new PmStatus();

			for (PmParameterDesc pMethod : paymentModeLs) {
				if (pMethod.getPmParameter().getParameterCode()
						.equals(SupplierManagementConst.ONLINE_PAYMENT)) {
					paymentMode = pMethod.getPmParameter();
				}
			}

			for (PmStatusDesc pStat : paymentStatusLs) {
				if (pStat
						.getPmStatus()
						.getStatusId()
						.equals(SupplierManagementConst.PM_STAT_PENDING_PAYMENT)) {
					paymentStatus = pStat.getPmStatus();
				}
			}

			payment.setPaymentMode(paymentMode);
			payment.setPmStatus(paymentStatus);

			PyBill pyBill = this.getManagedBean().getPyBill();
			pyBill.setPyPayments(new ArrayList<PyPayment>());
			List<PyPaymentDtl> pymDtlList = new ArrayList<PyPaymentDtl>();
			BigDecimal totalAmount = new BigDecimal(0);

			for (PyBillDtl newBillDtl : this.getManagedBean()
					.getNewPyBillDtlLs()) {
				PyPaymentDtl paymentDtl = new PyPaymentDtl();
				paymentDtl.setPyBillDtl(newBillDtl);
				paymentDtl.setPyPayment(payment);
				paymentDtl.setPaidAmt(newBillDtl.getItemPrice());
				setPaymentChargeLineByType(paymentDtl);
				pymDtlList.add(paymentDtl);
				totalAmount = totalAmount.add(newBillDtl.getItemPrice());
			}
			
			totalAmount = totalAmount.add(getManagedBean().getGstAmount())
					.setScale(2, BigDecimal.ROUND_UP);
			payment.setPyPaymentDtls(pymDtlList);
			payment.setPyBill(pyBill);
			payment.setPaymentAmt(totalAmount);
			payment.setPaymentDate(new Date());

			log.info("***Total Amount***" + totalAmount);

			// if(this.getManagedBean().getPaymentMode().equals(SupplierManagementConst.ONLINE_PAYMENT)){
			PyPaymentOrder pymtOrder = new PyPaymentOrder();
			pymtOrder.setPyPayment(payment);
			pymtOrder.setPaymentGateway("MOL");
			// pymtOrder.setChannelName("TESTING");

			payment.setPyPaymentOrder(pymtOrder);
			// }

			List<PyPayment> pyPaymentLs = new ArrayList<PyPayment>();
			pyPaymentLs.add(payment);
			pyBill.setPyPayments(pyPaymentLs);

			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setSmBill(pyBill);
			reqDTO.setSmAppl(getManagedBean().getSmAppl());

			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IPaymentSO.class);
			PaymentResDTO resDTO = new PaymentResDTO();
			resDTO = reqSO.createPayment(reqDTO);

			log.info("******Payment ID********"
					+ resDTO.getPyBill().getPyPayments().get(0).getPaymentId());

			/*
			 * if(this.getManagedBean().getPaymentMode().equals(
			 * SupplierManagementConst.OFFLINE_PAYMENT)){
			 * FacesContext.getCurrentInstance().addMessage(null,new
			 * FacesMessage
			 * (FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage
			 * (MessageConst.INFO_721051, MessageUtils.BUNDLE_COMMON_MESSAGES,
			 * new FacesScopes().getUserLocale())));
			 * 
			 * return "paymentAcknowledge";
			 * 
			 * }else{
			 */
			PmUser pmUser = new FacesScopes().getUserSessionBean();

			this.getManagedBean().setOrderId(
					resDTO.getPyBill().getPyPayments().get(0).getPaymentId());
			this.getManagedBean().setBillMobile(pmUser.getMobileFull());
			this.getManagedBean().setBillEmail(pmUser.getEmail());
			this.getManagedBean().setBillName(pmUser.getUserName());
			this.getManagedBean().setBillDesc("NGeP Payment");
			// this.getManagedBean().
			String orderId = resDTO.getPyBill().getPyPayments().get(0)
					.getPaymentId().toString();
			String vc = SmCommonUtil.md5(totalAmount + merchantId + orderId
					+ vKey);
			log.info("******VC********" + vc);
			BigDecimal ttlAmt = resDTO.getPyBill().getPyPayments().get(0)
					.getPaymentAmt();
			this.setvCode(vc);
			this.getManagedBean().setTotalAmount(ttlAmt);
			
			setPaymentHttpSession(this.getManagedBean().getTaskId(),this.getManagedBean().getBpmPojo());
			
/*			List paymentDtls = new ArrayList();
			paymentDtls.add(0, this.getManagedBean().getTaskId());
			paymentDtls.add(1, this.getManagedBean().getBpmPojo());
			paymentBean.getPaymentMap().put(orderId, paymentDtls);*/

			return "paymentRequestSubmission";
			// }
		}

		return "";
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
	public void responsePreRender() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

/*		PortletRequest pr = (PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap()
				.get("javax.portlet.request");
		Method method = pr.getClass()
				.getMethod("getOriginalHttpServletRequest");
		HttpServletRequest httpServletRequest = (HttpServletRequest) method
				.invoke(pr, new Object[] {});*/
		SmAppl appl = null;
		
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		HttpSession session = httpServletRequest.getSession();

		String orderId = httpServletRequest.getParameter("orderid");
		String tranID = httpServletRequest.getParameter("tranID");
		String status = httpServletRequest.getParameter("status");
		String domain = httpServletRequest.getParameter("domain");
		String amount = httpServletRequest.getParameter("amount");
		String currency = httpServletRequest.getParameter("currency");
		String appcode = httpServletRequest.getParameter("appcode");
		String paydate = httpServletRequest.getParameter("paydate");
		String skey = httpServletRequest.getParameter("skey");
		String channel = httpServletRequest.getParameter("channel") != null ? httpServletRequest
				.getParameter("channel") : null;
		String errorCode = httpServletRequest.getParameter("error_code") != null ? httpServletRequest
				.getParameter("error_code") : null;
		String errorDesc = httpServletRequest.getParameter("error_desc") != null ? httpServletRequest
				.getParameter("error_desc") : null;

		String paymentTaskId = null;
		Object paymentBpmPojo = null;

		//Log for testing httpsession
		getPaymentHttpSession();
		paymentTaskId = (String)session.getAttribute("paymentBpmTaskId");
		paymentBpmPojo = (Object)session.getAttribute("paymentBpmPojo");
		appl = (SmAppl)session.getAttribute("smAppl");
		
		log.info("paymentBean.getPaymentTaskId(): " + paymentTaskId);
		log.info("paymentBean.getPaymentBpmPojo(): " + paymentBpmPojo);
		this.getManagedBean().setTaskId(paymentTaskId);
		this.getManagedBean().setBpmPojo(paymentBpmPojo);

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
				PyPayment pyPayment = resDTO.getPyPayment();

				List<PmStatusDesc> pmStatLs = SmCommonUtil
						.retrievePmStatusList("SM",
								SupplierManagementConst.PM_STATUS_PAYMENT,
								new FacesScopes().getUserLocale().getLanguage());
				Map<Long, PmStatusDesc> pmStatusDescMap = SmCommonUtil
						.converPmStatusListToMap(pmStatLs);

				log.info("pmStatLs>>>>>" + pmStatLs.size());
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

					/*
					 * boolean test = (paymentId == ordId &&
					 * paymentAmount.compareTo(txAmount) == 0); boolean payOrd =
					 * (paymentId == ordId); boolean px =
					 * (paymentAmount.compareTo(txAmount) == 0);
					 * log.info("test<>>>>>" + test); log.info("payOrd<>>>>>" +
					 * payOrd); log.info("px<>>>>>" + px);
					 */

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

				PmStatusDesc pmStatDesc = pmStatusDescMap.get(statusCode);
				pyPayment.setPmStatus(pmStatDesc.getPmStatus());

				// updatePayment(smPayment,validPayment);
				Object token = getFacesScopes().getBPMAuthenticationCtxToken();
				PaymentReqDTO pymtReqDTO = new PaymentReqDTO();

				//pymtReqDTO.setPaidPersonnels(getPaidPersonnelBeforeProceed());
				
				pymtReqDTO.setValidPayment(validPayment);
				pymtReqDTO.setPyPayment(pyPayment);
				pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);

				pymtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				pymtReqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
				pymtReqDTO.setToken(token);

				FacesScopes fs = new FacesScopes();
				pymtReqDTO.setServerName(fs.getRequest().getServerName());
				pymtReqDTO.setServerPort(fs.getRequest().getServerPort());
				pymtReqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
				pymtReqDTO.setBackEndUpdate(false);
				pymtReqDTO.setRespondFromPaymentGateway(true);

				PaymentResDTO pymtResDTO = reqSO.updatePayment(pymtReqDTO);
				if(pymtResDTO.isSuccessful()){
					//to auto generate mof cert
					if(pyPayment.getPyBill().getBillType().
							equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_REG_FEE) && validPayment){
						try{
							SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(pyPayment.getPyBill().getBillRefId());
						}catch(Exception ex){
							log.info("<<<<Payment - error when generate Virtual Cert>>>>" + ex.getMessage());
						}
					}
					
					try {
						SmCommonUtil.paymentCurlIPN(httpServletRequest,true,merchantId,molIpnUrl);
					} catch (Exception ex) {
						log.error("curlIPN: " + ex.getMessage());
					}
				}
				
				showPaymentResponse(status, paymentId,errorDesc);
				this.renewSesionObject();
			} else {
				showPaymentResponse(
						SupplierManagementConst.PAYMENT_INVALID_RES, null,null);
				this.renewSesionObject();
			}
		} else {
			showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES,null,null);
			this.renewSesionObject();

		}

		session.removeAttribute("paymentBpmTaskId");
		session.removeAttribute("paymentBpmPojo");
		
		// paymentBean.setPaymentBpmPojo(null);
		// paymentBean.setPaymentTaskId(null);

/*		if (paymentBean.getPaymentMap().containsKey(orderId)) {
			paymentBean.getPaymentMap().remove(orderId);
		}*/

	}

	/**
	 * Redirect back from payment summary page to payment information page
	 * 
	 * @return String
	 */
	public String back() {
		return "portletViewMode.xhtml";
	}

	/**
	 * Get payment mode list for drop down
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getPaymentModeDropDown() {
		List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(SupplierManagementConst.PM_PAYMENT_MODE);
		for (SelectItem item : items) {
			if (item.getValue() != null) {
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * Get payment gateway response status
	 * 
	 * @param String
	 *            status
	 * @param Long
	 *            paymentId
	 */
	public void showPaymentResponse(String status, Long paymentId, String errorDesc) {
		FacesContext fc = FacesContext.getCurrentInstance();
		boolean paymentStatus = false;

		switch (status) {
		case SupplierManagementConst.MOL_SUCCESS:
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721059,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			paymentStatus = true;
			break;

		case SupplierManagementConst.MOL_FAIL:
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_721051,new String[]{errorDesc},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			break;

		case SupplierManagementConst.MOL_PENDING:
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721055,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			break;

		case SupplierManagementConst.PAYMENT_INVALID_RES:
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.EP_MAJ_00001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			break;

		}

		if (paymentStatus && paymentId != null) {
			PaymentAcknowledgeBackingBean paymentAckBB = new PaymentAcknowledgeBackingBean();
			paymentAckBB.getManagedBean().setPaymentId(paymentId);
			paymentAckBB.getManagedBean().setPaymentStatus(true);
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

	/**
	 * Create and update payment,bill,personnel and redirect to success page
	 */
	public void tempPaymentSuccess() {
		log.info("********submitPayment******");
		String paymentType = StringUtils.EMPTY;
		if (this.getManagedBean().getPyBill() != null) {
			List<PmStatusDesc> pmStatLs = SmCommonUtil.retrievePmStatusList(
					"SM", SupplierManagementConst.PM_STATUS_PAYMENT,
					new FacesScopes().getUserLocale().getLanguage());
			Map<Long, PmStatusDesc> pmStatusDescMap = SmCommonUtil
					.converPmStatusListToMap(pmStatLs);
			PmParameter paymentMode = new PmParameter();
			PyPayment payment = new PyPayment();
			PmStatus paymentStatus = new PmStatus();

			paymentMode = SmCommonUtil.retrieveParameterByTypeAndCode(
					SupplierManagementConst.PM_PAYMENT_MODE,
					SupplierManagementConst.ONLINE_PAYMENT, new FacesScopes()
							.getUserLocale().getLanguage());
			paymentStatus = pmStatusDescMap.get(
					SupplierManagementConst.PM_STAT_PENDING_PAYMENT)
					.getPmStatus();

			payment.setPaymentMode(paymentMode);
			payment.setPmStatus(paymentStatus);

			PyBill pyBill = this.getManagedBean().getPyBill();
			pyBill.setPyPayments(new ArrayList<PyPayment>());
			List<PyPaymentDtl> pymDtlList = new ArrayList<PyPaymentDtl>();
			BigDecimal totalAmount = new BigDecimal(0);

			for (PyBillDtl newBillDtl : this.getManagedBean()
					.getNewPyBillDtlLs()) {
				PyPaymentDtl paymentDtl = new PyPaymentDtl();
				paymentDtl.setPyBillDtl(newBillDtl);
				paymentDtl.setPyPayment(payment);
				paymentDtl.setPaidAmt(newBillDtl.getItemPrice());
				setPaymentChargeLineByType(paymentDtl);
				
				pymDtlList.add(paymentDtl);
				totalAmount = totalAmount.add(newBillDtl.getItemPrice());
				if(newBillDtl.getItemCode()!=null){
					paymentType = newBillDtl.getItemCode().getParameterCode();
				}
			}

			totalAmount = totalAmount.add(getManagedBean().getGstAmount())
					.setScale(2, BigDecimal.ROUND_UP);
			payment.setPyPaymentDtls(pymDtlList);
			payment.setPyBill(pyBill);
			payment.setPaymentAmt(totalAmount);
			payment.setPaymentDate(new Date());

			log.info("***Total Amount***" + totalAmount);

			// if(this.getManagedBean().getPaymentMode().equals(SupplierManagementConst.ONLINE_PAYMENT)){
			PyPaymentOrder pymtOrder = new PyPaymentOrder();
			pymtOrder.setPyPayment(payment);
			pymtOrder.setPaymentGateway("MOL");
			// pymtOrder.setChannelName("TESTING");

			payment.setPyPaymentOrder(pymtOrder);

			List<PyPayment> pyPaymentLs = new ArrayList<PyPayment>();
			pyPaymentLs.add(payment);
			pyBill.setPyPayments(pyPaymentLs);

			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setSmBill(pyBill);
			reqDTO.setPaymentType(paymentType);
			getManagedBean().getSmAppl().setPmStatus(paymentStatus);
			reqDTO.setSmAppl(getManagedBean().getSmAppl());

			// create Payment
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IPaymentSO.class);
			PaymentResDTO resDTO = new PaymentResDTO();
			resDTO = reqSO.createPayment(reqDTO);

			/*****************************************************************************************************************/
			// retrieve from smPayment
			IPaymentSO paymentSO = (IPaymentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPaymentSO.class);
			PaymentReqDTO paymentReqDTO = new PaymentReqDTO();
			Long ordId = Long.valueOf(resDTO.getPyBill().getPyPayments().get(0)
					.getPaymentId());
			BigDecimal txAmount = new BigDecimal(0);
			txAmount = totalAmount.setScale(2, BigDecimal.ROUND_UP);
			Long txID = Long.valueOf("123456");
			paymentReqDTO.setPaymentId(ordId);

			PaymentResDTO paymentResDTO = paymentSO
					.retrievePaymentInfoByPaymentId(paymentReqDTO);
			boolean validPayment = false;
			String status = "00";

			PyPayment pyPayment = paymentResDTO.getPyPayment();

			PyPaymentResponse smPaymentRes = new PyPaymentResponse();
			PyPaymentOrder paymentOrder = pyPayment.getPyPaymentOrder();
			
			log.info("Payment bill Details " + pyPayment.getPyBill().getPyBillDtls().size());
			
			Long statusCode = null;

			statusCode = SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT;
			validPayment = true;

			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			PaymentReqDTO pymtReqDTO = new PaymentReqDTO();

			// SmPaymentResponse smPaymentRes = new SmPaymentResponse();
			// SmPaymentOrder paymentOrder = smPayment.getSmPaymentOrder();

			PmStatusDesc pmStatDesc = pmStatusDescMap.get(statusCode);
			log.info(">>>>PaymentStatus ID>>>>"
					+ pmStatDesc.getPmStatus().getStatusId());
			pyPayment.setPmStatus(pmStatDesc.getPmStatus());

			paymentOrder.setChannelName("");
			smPaymentRes.setTransactionId(txID);
			smPaymentRes.setErrorCode("");
			smPaymentRes.setErrorDesc("");
			smPaymentRes.setTransactionDate(new Date());
			smPaymentRes.setPyPaymentOrder(paymentOrder);
			smPaymentRes.setPaymentStatus(status);
			smPaymentRes.setPaidAmt(txAmount);
			paymentOrder.getPyPaymentResponses().add(smPaymentRes);

			pymtReqDTO.setPaidPersonnels(getPaidPersonnelBeforeProceed());
			pymtReqDTO.setValidPayment(validPayment);
			pymtReqDTO.setPyPayment(pyPayment);
			pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);

			pymtReqDTO.setTaskId(this.getManagedBean().getTaskId());
			pymtReqDTO.setBpmPojo(getManagedBean().getBpmPojo());
			pymtReqDTO.setToken(token);

			FacesScopes fs = new FacesScopes();
			pymtReqDTO.setServerName(fs.getRequest().getServerName());
			pymtReqDTO.setServerPort(fs.getRequest().getServerPort());
			pymtReqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
			pymtReqDTO.setBackEndUpdate(false);
			pymtReqDTO.setPaymentType(paymentType);

			log.info("Debug payment>>>>>");
			
			PaymentResDTO pymtResDTO = reqSO.updatePayment(pymtReqDTO);

			showPaymentResponse(status, ordId, null);
			this.renewSesionObject();
		}

		/*
		 * String token = getFacesScopes().getBPMAuthenticationCtxToken();
		 * PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
		 * 
		 * pymtReqDTO.setSmAppl(this.getManagedBean().getSmAppl());
		 * pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
		 * pymtReqDTO.setTaskId(this.getManagedBean().getTaskId());
		 * pymtReqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		 * pymtReqDTO.setToken(token);
		 * 
		 * String paymentType = "";
		 * 
		 * for(SmBillDtl billDtl : this.getManagedBean().getNewSmBillDtlLs()){
		 * if(billDtl.getPmParameter().getParameterCode().equals("MF") ||
		 * billDtl.getPmParameter().getParameterCode().equals("RF")){
		 * paymentType = billDtl.getPmParameter().getParameterCode(); } }
		 * 
		 * pymtReqDTO.setPaymentType(paymentType);
		 * 
		 * try{ IPaymentSO reqSO = (IPaymentSO)
		 * BusinessDelegate.getInstance().getServiceObject("sm",
		 * IPaymentSO.class); reqSO.updatePaymentBpm(pymtReqDTO);
		 * 
		 * showPaymentResponse(SupplierManagementConst.MOL_SUCCESS);
		 * 
		 * }catch(Exception ex){ throw new EpException(ex.getMessage()); }
		 * 
		 * return null;
		 */
	}

	/**
	 * Get bill item description
	 * 
	 * @param PmParameter
	 *            pmParameter
	 * @return String
	 */
	public String getBillItemDescByLanguage(PmParameter pmParameter) {
		String desc = "";

		if (pmParameter != null) {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescList(
							pmParameter.getParameterType());
			if (parameterDescs != null) {
				for (PmParameterDesc paramDesc : parameterDescs) {
					if (paramDesc.getPmParameter() != null
							&& paramDesc.getPmParameter().getParameterId()
									.compareTo(pmParameter.getParameterId()) == 0) {
						desc = paramDesc.getCodeName();
						break;
					}
				}
			}
		}

		return desc;
	}

	/**
	 * Set softcert personnel list from bill
	 * 
	 * @param List
	 *            <SmBillDtl> smBillDtlLs
	 */
	private void setBilledSoftCertPersonnels(List<PyBillDtl> pyBillDtlLs) {
		this.getManagedBean().setSelectedSoftCertLs(new SmPersonnel[0]);
		this.getManagedBean().setSoftCertReqPersonnels(
				new ArrayList<SmPersonnel>());
		for (PyBillDtl billedPersonnel : pyBillDtlLs) {
			//TODO: new payment design
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setPersonnelId(billedPersonnel.getBillDtlRefId());
			SmPersonnel personnel = SmServiceLookup.getPersonnelSO().retrievePersonnelById(reqDTO).getSmPersonnel();
			
			if(personnel != null){
				if (personnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ) {
					this.getManagedBean().getSoftCertReqPersonnels()
					.add(personnel);
				}
			}
		}
	}

	/**
	 * Get paid personnel for soft certificate in payment summary
	 * 
	 * @return List<SmPersonnel>
	 */
	private List<SmPersonnel> getPaidPersonnelBeforeProceed() {
		List<SmPersonnel> paidPersonnels = new ArrayList<SmPersonnel>();
		if (getManagedBean().getSelectedSoftCertLs() != null
				&& getManagedBean().getSelectedSoftCertLs().length > 0) {
			paidPersonnels = Arrays.asList(getManagedBean()
					.getSelectedSoftCertLs());
		}
		return paidPersonnels;
	}

/*	public PaymentBean getPaymentBean() {
		return paymentBean;
	}

	public void setPaymentBean(PaymentBean paymentBean) {
		this.paymentBean = paymentBean;
	}*/
	
	private void setPaymentHttpSession(String taskId, Object bpmPojo){
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		
		HttpSession session = httpServletRequest.getSession();
		//session.setAttribute("paymentBpmTaskId", this.getManagedBean().getTaskId());
		//session.setAttribute("paymentBpmPojo", this.getManagedBean().getBpmPojo());
		session.setAttribute("paymentBpmTaskId", taskId);
		session.setAttribute("paymentBpmPojo", bpmPojo);
		session.setAttribute("smAppl", getManagedBean().getSmAppl());
	}
	
	private void getPaymentHttpSession(){
		log.info("***********setPaymentHttpSession**********");
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		
		HttpSession session = httpServletRequest.getSession();
		//session.setAttribute("paymentBpmTaskId", this.getManagedBean().getTaskId());
		//session.setAttribute("paymentBpmPojo", this.getManagedBean().getBpmPojo());
		String taskId = (String)session.getAttribute("paymentBpmTaskId");
		Object bpmPojo = (Object)session.getAttribute("paymentBpmPojo");
		
		log.info("getPaymentHttpSession taskId>>>>>>>" + taskId);
		log.info("getPaymentHttpSession bpmPojo>>>>>>>" + bpmPojo);
		
//		session.removeAttribute("paymentBpmTaskId");
//		session.removeAttribute("paymentBpmPojo");
		
	}
	
	//TODO: new payment design
	/**
	 * Retrieve SmAppl By applId
	 * @param applId
	 * @return SmAppl
	 */
	private SmAppl retriveSmApplById(Long applId){
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		MofRegistrationResDTO resDTO = SmServiceLookup.getMofRegistrationSO().retrieveSmApplicationDetailsById(reqDTO);
		
		SmAppl smAppl = resDTO.getSmAppl();
		
		return smAppl;
	}
	
	//TODO: new payment design
	/**
	 * retrieve personnel by BillDtl Ref Id
	 * @param billDtlRefId
	 * @return smPersonnel
	 */
	public SmPersonnel retrievePersonnelByPersonnelId(Long personnelId){
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setPersonnelId(personnelId);
		SmPersonnel personnel = SmServiceLookup.getPersonnelSO().retrievePersonnelById(reqDTO).getSmPersonnel();
		
		return personnel;
	}
	
	/**
	 * set Payment Charge Line by Type
	 */
	private void setPaymentChargeLineByType(PyPaymentDtl paymentDtl){
		String itemCode = paymentDtl.getPyBillDtl().getItemCode().getParameterCode();
		
		log.info("****Item Code****" + itemCode);
		log.info("****Item Price****" +paymentDtl.getPyBillDtl().getItemPrice());
		
		if(itemCode.equals(SupplierManagementConst.MOF_PAYMENT) || itemCode.equals(SupplierManagementConst.RENEWAL_PAYMENT)){
			List<PyPaymentChargeLine> paymentChargeLineLs = new ArrayList<PyPaymentChargeLine>();
			PyPaymentChargeLine hChargeLine = new PyPaymentChargeLine();
			PyPaymentChargeLine lChargeLine = new PyPaymentChargeLine();
			
			BigDecimal cdcCharge = SmServiceLookup.getSmRuleSO().retrieveMofAmountPaidToCDC().getMofAmountPaidToCDC();
			
			ConfigGeneralReqDTO reqDTO =  new ConfigGeneralReqDTO();
			ConfigGeneralResDTO resDTO = SmServiceLookup.getConfigGeneralSO().getConfigGeneral(reqDTO);
			
			hChargeLine.setPmGlAccount(resDTO.getPmConfigGeneral().getRegGovGlAcc());
			hChargeLine.setPyPaymentDtl(paymentDtl);
			hChargeLine.setChargeLineAmt(paymentDtl.getPyBillDtl().getItemPrice().subtract(cdcCharge));
			
			lChargeLine.setPmGlAccount(resDTO.getPmConfigGeneral().getRegSupGlAcc());
			lChargeLine.setPyPaymentDtl(paymentDtl);
			lChargeLine.setChargeLineAmt(cdcCharge);
			
			paymentChargeLineLs.add(hChargeLine);
			paymentChargeLineLs.add(lChargeLine);
			
			paymentDtl.setPyPaymentChargeLines(paymentChargeLineLs);
			
		}else if(itemCode.equals(SupplierManagementConst.PROCESSING_PAYMENT)){
			List<PyPaymentChargeLine> paymentChargeLineLs = new ArrayList<PyPaymentChargeLine>();
			PyPaymentChargeLine hChargeLine = new PyPaymentChargeLine();
			
			ConfigGeneralReqDTO reqDTO =  new ConfigGeneralReqDTO();
			ConfigGeneralResDTO resDTO = SmServiceLookup.getConfigGeneralSO().getConfigGeneral(reqDTO);
			
			hChargeLine.setPmGlAccount(resDTO.getPmConfigGeneral().getRegGovGlAcc());
			hChargeLine.setPyPaymentDtl(paymentDtl);
			hChargeLine.setChargeLineAmt(paymentDtl.getPyBillDtl().getItemPrice());
			
			paymentChargeLineLs.add(hChargeLine);
			paymentDtl.setPyPaymentChargeLines(paymentChargeLineLs);
		}
	
	}
	
	private String getSupplierRoleByUserId(SmAppl appl,Long userId) {
		String retVal = StringUtils.EMPTY;
		if (userId != null
				&& !EntityHelper.isCollectionEmpty(appl
						.getSmPersonnels())) {
			for (SmPersonnel personnel : appl
					.getSmPersonnels()) {
				if (personnel.getPmUser() != null
						&& personnel.getPmUser().getUserId() != null
						&& personnel.getPmUser().getUserId().compareTo(userId) == 0) {
					retVal = personnel.getEpRole();
				}
			}
		}
		return retVal;
	}
	
	
}
