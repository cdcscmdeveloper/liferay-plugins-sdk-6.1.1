/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.paymentmatch;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.invoicepaymentdata.FLInvoiceAndPaymentDataType;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.paymentmatch.PaymentMatchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Payment Match Backing Bean
 * @author KK LIM
 * @since 30/10/2013
 *
 */
@ManagedBean(name = "paymentMatchBackingBean")
@RequestScoped
public class PaymentMatchBackingBean extends BaseBackingBean<PaymentMatchManagedBean>{

	private static Logger log = Logger.getLogger(PaymentMatchBackingBean.class);
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	

	public String submitAction(String action){
		IPaymentMatchSO paymentSO = null;
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlPaymentAdvice paymentAdvice = getManagedBean().getPaymentAdvice();
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject("fl",IPaymentMatchSO.class);

			reqDTO.setPaymentAdvice(paymentAdvice);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			System.out.println("action: "+action);
			FulfilmentResDTO resDTO = paymentSO.createPaymentMatch(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Payment Advice Id. : "+paymentAdvice.getPaymentAdviceId();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}finally {
			paymentSO = null;
		}
		return null;
	}

	public void preRenderView() {
		//		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		//		String loginId = originalServletRequest.getParameter("loginId");
		System.out.println("taskId:" + taskId);
		//		System.out.println("loginId:" + loginId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		FLInvoiceAndPaymentDataType paymentObj = (FLInvoiceAndPaymentDataType)obj;
		flReqDTO.setFlOrderId(paymentObj.getFullfillmentRequestId());
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);

		resDTO = invoiceSO.retrieveInvoiceByFlOrderId(flReqDTO);
		FlPaymentAdvice paymentAdvice = new FlPaymentAdvice();
		paymentAdvice.setFlInvoice(resDTO.getInvoice());

		//				PaymentMatchBackingBean paymentBB = new PaymentMatchBackingBean();
		getManagedBean().setPaymentAdvice(paymentAdvice);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setOutComes(res.getAction());

		//		FacesContext fc = FacesContext.getCurrentInstance();
		//	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/fl/invoice/invBasePage.xhtml");
	}
}
