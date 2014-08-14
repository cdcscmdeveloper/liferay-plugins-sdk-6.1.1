package my.ep.web.backingbean.sm.payment;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmBill;
import my.ep.persistence.entity.sm.SmBillDtl;
import my.ep.persistence.entity.sm.SmPayment;
import my.ep.persistence.entity.sm.SmPaymentDtl;
import my.ep.persistence.entity.sm.SmPaymentOrder;
import my.ep.persistence.entity.sm.SmPaymentResponse;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.payment.PaymentManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean (name = "paymentBackingBean")
@RequestScoped
public class PaymentBackingBean extends BaseBackingBean<PaymentManagedBean>{

	public static Logger log = Logger.getLogger(PaymentBackingBean.class);
	private final String merchantId = EpPropertiesUtils.getInstance().getProperty("molpay.merchantid");
	private final String vKey = EpPropertiesUtils.getInstance().getProperty("molpay.vkey");
	private final String paymentGatewayUrl = EpPropertiesUtils.getInstance().getProperty("molpay.url") + merchantId + "/";
	private final String molIpnUrl =  EpPropertiesUtils.getInstance().getProperty("molpay.ipnurl");
	private String vCode;
	
	private Map<String, PmParameterDesc> supplierPaymentItemMap;

	public PaymentBackingBean(){
		this.getManagedBean().setCountry("MY");
//		PmUser pmUser = new FacesScopes().getUserSessionBean();
//		if (pmUser != null && pmUser.getUserId() != null) {
//			System.out.println("***userId: " + pmUser.getUserId());
//
//			new LogKey(pmUser.getUserId()); 
//		}else{
//			new LogKey(1L); 
//		}
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
	 * @param vCode the vCode to set
	 */
	public void setvCode(String vCode) {
		this.vCode = vCode;
	}
	
	public void retrievePaymentItem(){
		List<PmParameterDesc> pmParameterDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.PM_PAYMENT_ITEM, new FacesScopes().getUserLocale().getLanguage());
		supplierPaymentItemMap = new HashMap<String, PmParameterDesc>();
		
		for(PmParameterDesc pmDesc : pmParameterDesc){
			supplierPaymentItemMap.put(pmDesc.getPmParameter().getParameterCode(), pmDesc);
		}
	}

	public void confirmPayment(){

	}

	public void retrieveBillInfo(){
		/*
		 * for testing Purpose
		 */
		System.out.println(">>>>>>>InitPayment Stert>>>>");
		/*MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
		mofReqDTO.setApplId(1009);
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationResDTO mofResDTO  = mofRegistrationSO.retrieveSupplierProfileByApplId(mofReqDTO);
		this.getManagedBean().setSmAppl(mofResDTO.getSmAppl());
		this.getManagedBean().setAppId(mofReqDTO.getApplId());*/ //dummy test

		//this.getManagedBean().setAppId(1009L);
		log.info("*******retrieveBillInfo Start******");
		IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		reqDTO.setApplId(this.getManagedBean().getAppId());
		PaymentResDTO resDTO = reqSO.retrieveSmBillByApplId(reqDTO);
		this.getManagedBean().setSmAppl(resDTO.getSmBill().getSmAppl());
		
		log.info("*******Bill Object**********" + resDTO.getSmBill().getBillId());
		this.getManagedBean().setSmBill(resDTO.getSmBill());
		Long billId = resDTO.getSmBill().getBillId();
		List<SmBillDtl> smBillDtls = resDTO.getSmBill().getSmBillDtls();

		
		if(resDTO.getSmBill().getSmPayments().size() > 0){
			//System.out.println("******check is it Offline Payment*****");
			/*if(checkOfflinePayment(billId)){
				//this.getManagedBean().setOfflinePayment(true);
				System.out.println("*****is Offline Payment*****");
				BigDecimal ttlAmount = new BigDecimal(0);
				
				this.getManagedBean().setNewSmBillDtlLs(new ArrayList<SmBillDtl>());
				for(SmBillDtl billDtl : smBillDtls){
					ttlAmount = ttlAmount.add(billDtl.getItemPrice());
					this.getManagedBean().getNewSmBillDtlLs().add(billDtl);
				}
			
				System.out.println("TotalAmount>>>>>>" + ttlAmount);
				this.getManagedBean().setTotalAmount(ttlAmount);
				
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/payment/paymentSummary.xhtml");		
			}else{*/
				//this.getManagedBean().setOfflinePayment(false);
				System.out.println("*****is Online Payment*****");
				
				proceedToItemList(smBillDtls);
			//}
		}else{
			proceedToItemList(smBillDtls);
		}
	}
	
	public void proceedToItemList(List<SmBillDtl> smBillDtlLs){
		ArrayList<SmPersonnel> billDtlLs = new ArrayList<SmPersonnel>();
		this.getManagedBean().setDefaultCertPaymentMap(new HashMap<Long,SmBillDtl>());
		for(SmBillDtl billDtl : smBillDtlLs){
			if(billDtl.getPmParameter().getParameterCode().equals(SupplierManagementConst.SOFTCERT_PAYMENT)){
				billDtlLs.add(billDtl.getSmPersonnel());
				this.getManagedBean().getDefaultCertPaymentMap().put(billDtl.getSmPersonnel().getPersonnelId(), billDtl);
			}else{
				//this.getManagedBean().setApplicationPayment(new SmBillDtl());
				this.getManagedBean().setApplicationPayment(billDtl);
			}
		}

		//SmBillDtl[] billDtlArr = (SmBillDtl[]) billDtlLs.toArray();
		this.getManagedBean().setSelectedSoftCertLs(billDtlLs.toArray(new SmPersonnel[billDtlLs.size()]));
		System.out.println("**********SelectedSoftCertLs****" + this.getManagedBean().getSelectedSoftCertLs().length);


		if(this.getManagedBean().getSelectedSoftCertLs() != null && this.getManagedBean().getSelectedSoftCertLs().length > 0){
			this.getManagedBean().setSoftCertReqPersonnels(new ArrayList<SmPersonnel>());
			for(SmPersonnel softcertPersonnel : this.getManagedBean().getSmAppl().getSmPersonnels()){
				if(softcertPersonnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ){
					this.getManagedBean().getSoftCertReqPersonnels().add(softcertPersonnel);
				}
			}
		}


		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/payment/portletViewMode.xhtml");
	}
	
	public String proceedToPaymentSummary(){
		System.out.println("****proceedToPaymentSummary****");

		SmBill smBill = this.getManagedBean().getSmBill();
		BigDecimal ttlAmount = new BigDecimal(0);
		System.out.println("****MAP SIE****" + this.getManagedBean().getDefaultCertPaymentMap().size());
		this.getManagedBean().setNewSmBillDtlLs(new ArrayList<SmBillDtl>());
		retrievePaymentItem();
		
		Map<Long,SmPersonnel> selectedSoftCertMap = new HashMap<Long,SmPersonnel>();
		for(SmPersonnel selectedPersonnel : this.getManagedBean().getSelectedSoftCertLs()){
			selectedSoftCertMap.put(selectedPersonnel.getPersonnelId(), selectedPersonnel);
		}
		
		
		for(SmBillDtl billDtl : smBill.getSmBillDtls()){
			boolean nonExistBillDtl = false;
			
			if(billDtl.getPmParameter().getParameterCode().equals(SupplierManagementConst.SOFTCERT_PAYMENT)){
				for(SmPersonnel scPersonnel : this.getManagedBean().getSelectedSoftCertLs()){
					System.out.println("*******>>>" + scPersonnel + "<<<<********");
					if(!this.getManagedBean().getDefaultCertPaymentMap().containsKey(scPersonnel.getPersonnelId())){
						nonExistBillDtl = true;
						break;
					}
				}

				if(nonExistBillDtl){
					SmBillDtl  newScPersonnel = new SmBillDtl();
					PmParameterDesc pmParamDesc = supplierPaymentItemMap.get("SF");
					newScPersonnel.setPmParameter(pmParamDesc.getPmParameter());
					newScPersonnel.setItemDesc(pmParamDesc.getCodeDesc());
					newScPersonnel.setItemPrice(new BigDecimal(150.00));

					newScPersonnel.setSmBill(smBill);
					newScPersonnel.setSmPersonnel(billDtl.getSmPersonnel());

					this.getManagedBean().getNewSmBillDtlLs().add(newScPersonnel);
				}else{
					if(selectedSoftCertMap.containsKey(billDtl.getSmPersonnel().getPersonnelId())){
						this.getManagedBean().getNewSmBillDtlLs().add(billDtl);
					}
				}

			}else{
				this.getManagedBean().getNewSmBillDtlLs().add(billDtl);
			}
		}
		
		for(SmBillDtl newItemDtlLs : this.getManagedBean().getNewSmBillDtlLs()){
			ttlAmount = ttlAmount.add(newItemDtlLs.getItemPrice());
			System.out.println("TotalAmount>>>>>>" + ttlAmount);
		}
	
		this.getManagedBean().setTotalAmount(ttlAmount);
//		for(SmPersonnel personnel : this.getManagedBean().getSoftCertReqPersonnels()){
//			for(SmPersonnel scPersonnel : this.getManagedBean().getSelectedSoftCertLs()){
//				if(scPersonnel.getPersonnelId() == personnel.get)
//			}
//		}
		return "paymentSummary";
	}
	
	public String submitPayment(){
		System.out.println("********submitPayment******");
		if(this.getManagedBean().getSmBill() != null){
			List<PmParameterDesc> paymentModeLs =SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.PM_PAYMENT_MODE, new FacesScopes().getUserLocale().getLanguage());
			List<PmStatusDesc> paymentStatusLs = SmCommonUtil.retrievePmStatusList("SM",SupplierManagementConst.PM_STATUS_PAYMENT, new FacesScopes().getUserLocale().getLanguage());
			PmParameter paymentMode = new PmParameter();
			SmPayment payment = new SmPayment();
			PmStatus paymentStatus = new PmStatus();
			
			for(PmParameterDesc pMethod : paymentModeLs){
				if(pMethod.getPmParameter().getParameterCode().equals(SupplierManagementConst.ONLINE_PAYMENT)){
					paymentMode = pMethod.getPmParameter();
				}
			}
			
			for(PmStatusDesc pStat : paymentStatusLs){
				if(pStat.getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STAT_PENDING_PAYMENT)){
					paymentStatus = pStat.getPmStatus();
				}
			}
			
			payment.setPmParameter1(paymentMode);
			payment.setPmStatus(paymentStatus);

			SmBill smBill = this.getManagedBean().getSmBill();
			smBill.setSmPayments(new ArrayList<SmPayment>());
			List<SmPaymentDtl> pymDtlList = new ArrayList<SmPaymentDtl>();
			BigDecimal totalAmount = new BigDecimal(0);

			for(SmBillDtl newBillDtl : this.getManagedBean().getNewSmBillDtlLs()){
				SmPaymentDtl paymentDtl = new SmPaymentDtl();
				paymentDtl.setSmBillDtl(newBillDtl);
				paymentDtl.setSmPayment(payment);
				pymDtlList.add(paymentDtl);
				totalAmount = totalAmount.add(newBillDtl.getItemPrice());
			}
			
			totalAmount = totalAmount.setScale(2,BigDecimal.ROUND_UP);
			payment.setSmPaymentDtls(pymDtlList);
			payment.setSmBill(smBill);
			payment.setPaymentAmt(totalAmount);

			System.out.println("***Total Amount***" +totalAmount);

//			if(this.getManagedBean().getPaymentMode().equals(SupplierManagementConst.ONLINE_PAYMENT)){
			SmPaymentOrder pymtOrder = new SmPaymentOrder();
			pymtOrder.setSmPayment(payment);
			pymtOrder.setPaymentGateway("MOL");
			//pymtOrder.setChannelName("TESTING");

			payment.setSmPaymentOrder(pymtOrder);
//			}

			List<SmPayment> smPaymentLs = new ArrayList<SmPayment>();
			smPaymentLs.add(payment);
			smBill.setSmPayments(smPaymentLs);
			
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setSmBill(smBill);
			
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
			PaymentResDTO resDTO = new PaymentResDTO();
			resDTO = reqSO.createPayment(reqDTO);
			
			System.out.println("******Payment ID********" + resDTO.getSmBill().getSmPayments().get(0).getPaymentId());
			
/*			if(this.getManagedBean().getPaymentMode().equals(SupplierManagementConst.OFFLINE_PAYMENT)){
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721051,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));

				return "paymentAcknowledge";

			}else{*/
				PmUser pmUser = new FacesScopes().getUserSessionBean();

				PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().setAttribute("paymentTaskId", this.getManagedBean().getTaskId());
				PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().setAttribute("paymentBpmPojo", this.getManagedBean().getBpmPojo());
				
				this.getManagedBean().setOrderId(resDTO.getSmBill().getSmPayments().get(0).getPaymentId());
				this.getManagedBean().setBillMobile(pmUser.getMobileNo());
				this.getManagedBean().setBillEmail(pmUser.getEmail());
				this.getManagedBean().setBillName(pmUser.getUserName());
				this.getManagedBean().setBillDesc("NGeP Payment");
				//this.getManagedBean().
				String orderId = resDTO.getSmBill().getSmPayments().get(0).getPaymentId().toString();
				String vc = SmCommonUtil.md5(totalAmount + merchantId + orderId + vKey);
				System.out.println("******VC********" + vc);
				BigDecimal ttlAmt = resDTO.getSmBill().getSmPayments().get(0).getPaymentAmt();
				this.setvCode(vc);
				this.getManagedBean().setTotalAmount(ttlAmt);
				
				String taskId = (String)PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().getAttribute("paymentTaskId");
				Object bpmPojo = (Object)PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().getAttribute("paymentBpmPojo");
				
				System.out.println("*****taskId*****" + taskId);
				System.out.println("*****bpmPojo*****" + bpmPojo);
				
				return "paymentRequestSubmission";
			//}
		}
		
		return "";
	}
	
	public void responsePreRender() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String taskId = (String)PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().getAttribute("paymentTaskId");
		Object bpmPojo = (Object)PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().getAttribute("paymentBpmPojo");
		
		
		log.info("************responsePreRender Start********");
		log.info("-----------BPM TaskId-----------" + taskId);
		log.info("-----------BPM POJO-----------" + bpmPojo);
		
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setBpmPojo(bpmPojo);
		
		PortletRequest pr = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.portlet.request");
		Method method = pr.getClass().getMethod("getOriginalHttpServletRequest");
		HttpServletRequest httpServletRequest = (HttpServletRequest)method.invoke(pr, new Object[] {});
		
		String orderId = httpServletRequest.getParameter("orderid");
		String tranID = httpServletRequest.getParameter("tranID");
		String status = httpServletRequest.getParameter("status");
		String domain = httpServletRequest.getParameter("domain");
		String amount = httpServletRequest.getParameter("amount");
		String currency = httpServletRequest.getParameter("currency");
		String appcode = httpServletRequest.getParameter("appcode");
		String paydate = httpServletRequest.getParameter("paydate");
		String skey = httpServletRequest.getParameter("skey");
		String channel = httpServletRequest.getParameter("channel") != null ?  httpServletRequest.getParameter("channel") : null;
		String errorCode = httpServletRequest.getParameter("error_code") != null ?  httpServletRequest.getParameter("error_code") : null;
		String errorDesc = httpServletRequest.getParameter("error_desc") != null ?  httpServletRequest.getParameter("error_desc") : null;
		//String errorCode = null;
		//String errorDesc = null;
		//String nbcp
		
		try{
			curlIPN(httpServletRequest);
		}catch(Exception ex){
			System.out.println("curlIPN: "+ ex.getMessage());
		}
		
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
		
		//To verify the data integrity sending by MOLPay
		String key0 = SmCommonUtil.md5(tranID + orderId + status + domain + amount + currency);
		String key1 = SmCommonUtil.md5(paydate + domain + key0 + appcode + vKey);
		
		System.out.println("SKey>>>>" + skey);
		System.out.println("Key1>>>>" + key1);
		if(skey.equals(key1)){
			System.out.println("SKey & Key1 Are Match>>>>");
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			Long ordId = Long.valueOf(orderId);
			BigDecimal txAmount = new BigDecimal(amount);
			Long txID = Long.valueOf(tranID);
			reqDTO.setPaymentId(ordId);
			PaymentResDTO resDTO = reqSO.retrievePaymentInfoByPaymentId(reqDTO);
			boolean validPayment = false;
			
			if(resDTO.getSmPayment() != null){
				SmPayment smPayment = resDTO.getSmPayment();
				
				List<PmStatusDesc> pmStatLs = SmCommonUtil.retrievePmStatusList("SM",SupplierManagementConst.PM_STATUS_PAYMENT, new FacesScopes().getUserLocale().getLanguage());
				Map<Long,PmStatusDesc> pmStatusDescMap = SmCommonUtil.converPmStatusListToMap(pmStatLs);
				
				System.out.println("pmStatLs>>>>>" + pmStatLs.size());
				SmPaymentResponse smPaymentRes = new SmPaymentResponse();
				SmPaymentOrder paymentOrder = smPayment.getSmPaymentOrder();
				Long statusCode = null;
				
				log.info("Status Success 0>>>>>");
				if(status.equals(SupplierManagementConst.MOL_SUCCESS)){
					Long paymentId = smPayment.getPaymentId();
					BigDecimal paymentAmount = smPayment.getPaymentAmt();
					
					System.out.println("paymentId>>>>" + paymentId);
					System.out.println("orderId>>>>" + orderId);
					System.out.println("paymentAmount>>>>" + paymentAmount);
					System.out.println("txAmount>>>>" + txAmount);
					
					boolean test = (paymentId == ordId && paymentAmount.compareTo(txAmount) == 0);
					boolean payOrd = (paymentId == ordId);
					boolean px = (paymentAmount.compareTo(txAmount) == 0);
					System.out.println("test<>>>>>" + test);
					System.out.println("payOrd<>>>>>" + payOrd);
					System.out.println("px<>>>>>" + px);
					
					if(paymentId.longValue() == ordId.longValue() && paymentAmount.compareTo(txAmount) == 0){
						System.out.println("current status>>>>" + smPayment.getPmStatus().getStatusId());
						if(smPayment.getPmStatus().getStatusId() == SupplierManagementConst.PM_STAT_PENDING_PAYMENT || 
								smPayment.getPmStatus().getStatusId() == SupplierManagementConst.PM_STAT_FAILED_PAYMENT){
							System.out.println("current payment>>>>" + smPayment.getPmStatus().getStatusId());
							
							if(!smPayment.getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT)){
								statusCode = SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT;
								validPayment = true;
							}else{
								statusCode = smPayment.getPmStatus().getStatusId();
							}
						}
						//						log.info("Payment Success>>>>>" + orderId);
						//						statusCode = SupplierManagementConst.PM_STAT_SUCCESS_PAYMENT;
						//						validPayment = true;
					}
					else{
						statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;
					}
				}else if(status.equals(SupplierManagementConst.MOL_PENDING)){
					statusCode = SupplierManagementConst.PM_STAT_PENDING_PAYMENT;
					
				}else if(status.equals(SupplierManagementConst.MOL_FAIL)){
					statusCode = SupplierManagementConst.PM_STAT_FAILED_PAYMENT;
				}
				
				Date txDate;
				try {
					txDate = DateUtils.convertStringToDate("yyyy-MM-dd HH:mm:ss",paydate);
				} catch (Exception e) {
					txDate = new Date();
					e.printStackTrace();
				}
				paymentOrder.setChannelName(channel);
				smPaymentRes.setTransactionId(txID);
				smPaymentRes.setErrorCode(errorCode);
				smPaymentRes.setErrorDesc(errorDesc);
				smPaymentRes.setTransactionDate(txDate);
				smPaymentRes.setSmPaymentOrder(paymentOrder);
				smPaymentRes.setPaymentStatus(status);
				smPaymentRes.setPaidAmt(txAmount);
				paymentOrder.getSmPaymentResponses().add(smPaymentRes);
				
				PmStatusDesc pmStatDesc = pmStatusDescMap.get(statusCode);
				smPayment.setPmStatus(pmStatDesc.getPmStatus());
				
				//updatePayment(smPayment,validPayment);
				String token = getFacesScopes().getBPMAuthenticationCtxToken();
				PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
				
				pymtReqDTO.setValidPayment(validPayment);
				pymtReqDTO.setSmPayment(smPayment);
				pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);

				pymtReqDTO.setTaskId(this.getManagedBean().getTaskId()); 
				pymtReqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
				pymtReqDTO.setToken(token);
				
				FacesScopes fs = new FacesScopes();
				pymtReqDTO.setServerName(fs.getRequest().getServerName());
				pymtReqDTO.setServerPort(fs.getRequest().getServerPort());
				
				PaymentResDTO pymtResDTO = reqSO.updatePayment(pymtReqDTO);
				
				showPaymentResponse(status);
				this.renewSesionObject();
			}else{
				showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES);
				this.renewSesionObject();
			}
		}else{
			showPaymentResponse(SupplierManagementConst.PAYMENT_INVALID_RES);		
			this.renewSesionObject();
		}
		PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().removeAttribute("paymentTaskId");
		PortalUtil.getHttpServletRequest(new FacesScopes().getRequest()).getSession().removeAttribute("paymentBpmPojo");
	}
	
	private void curlIPN(HttpServletRequest httpServletRequest) throws Exception{
		Enumeration<String> resParameters = httpServletRequest.getParameterNames();
		//List<NameValuePair> params = new ArrayList<NameValuePair>();
		StringBuilder strParams = new StringBuilder();
		
		while(resParameters.hasMoreElements()){
			String paramName = (String)resParameters.nextElement();
			//params.add(new BasicNameValuePair(paramName, httpServletRequest.getParameter(paramName)));
			strParams.append(paramName + "=" + httpServletRequest.getParameter(paramName));
			strParams.append("&");
			System.out.println(paramName + " - " + httpServletRequest.getParameter(paramName));
		}
		strParams.append("treq=1");
		System.out.println("StrParams>>>>>>" + strParams);
		//URL url = null;
		//BufferedReader reader = null;

		try
		{
			// create the HttpURLConnection
			URL url = new URL(molIpnUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// just want to do an HTTP POST here
			connection.setRequestMethod("POST");

			// uncomment this if you want to write output to this url
			connection.setDoOutput(true);
			
			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(strParams.toString());
			writer.flush();
			writer.close();
			os.close();
			// give it 15 seconds to respond
			/*connection.setReadTimeout(15*1000);
			connection.connect();
			
			StringBuilder stringBuilder;
			// read the output from the server
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			stringBuilder = new StringBuilder();
			
			System.out.println("***********CURL Response*********");
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				stringBuilder.append(line + "\n");
			}
			System.out.println(">>>"+ stringBuilder.toString());*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//throw e;
		}
	}
	
	/*private boolean checkOfflinePayment(Long billId){
		boolean result = false;
		
		IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		reqDTO.setBillId(billId);
		reqDTO.setPaymentMode(SupplierManagementConst.OFFLINE_PAYMENT);
		
		PaymentResDTO resDTO = reqSO.retrievePaymentByBillIdAndMode(reqDTO);
		
		if(resDTO.getSmPaymentLs().size() > 0){
			result = true;
		}
		
		return result;
	}*/
	
	public List<SelectItem> getPaymentModeDropDown(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(SupplierManagementConst.PM_PAYMENT_MODE);
		for(SelectItem item : items) {
			if(item.getValue()!=null) {			
				list.add(item);
			}
		}
		return list;
    }
	
	public void showPaymentResponse(String status){
		FacesContext fc = FacesContext.getCurrentInstance();
		
		switch (status) {
		case SupplierManagementConst.MOL_SUCCESS : 
			fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721059,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			break;

		case SupplierManagementConst.MOL_FAIL : 
			fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,MessageUtils.getMessage(MessageConst.ERROR_721051,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			break;

		case SupplierManagementConst.MOL_PENDING :
			fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721055,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			break;

		case SupplierManagementConst.PAYMENT_INVALID_RES :
			fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,MessageUtils.getMessage(MessageConst.EP_MAJ_00001,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			break;

		}
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/payment/paymentAcknowledge.xhtml");
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String tempPaymentSuccess(){
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		PaymentReqDTO pymtReqDTO = new PaymentReqDTO();
		
		pymtReqDTO.setSmAppl(this.getManagedBean().getSmAppl());
		pymtReqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
		pymtReqDTO.setTaskId(this.getManagedBean().getTaskId());
		pymtReqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		pymtReqDTO.setToken(token);
		
		String paymentType = "";
		
		for(SmBillDtl billDtl : this.getManagedBean().getNewSmBillDtlLs()){
			if(billDtl.getPmParameter().getParameterCode().equals("MF") || billDtl.getPmParameter().getParameterCode().equals("RF")){
				paymentType = billDtl.getPmParameter().getParameterCode();
			}
		}
		
		pymtReqDTO.setPaymentType(paymentType);
		
		try{
			IPaymentSO reqSO = (IPaymentSO) BusinessDelegate.getInstance().getServiceObject("sm", IPaymentSO.class);
			reqSO.updatePaymentBpm(pymtReqDTO);
			
			showPaymentResponse(SupplierManagementConst.MOL_SUCCESS);

		}catch(Exception ex){
			throw new EpException(ex.getMessage());
		}
	
		return null;
	}
}

