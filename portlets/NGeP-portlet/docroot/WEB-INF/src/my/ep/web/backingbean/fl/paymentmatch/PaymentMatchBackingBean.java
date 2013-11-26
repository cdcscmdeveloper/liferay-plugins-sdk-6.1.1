/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.paymentmatch;

import java.text.ParseException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.paymentmatch.PaymentMatchManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
	
	public String submitAction(String action) throws ParseException {
		IPaymentMatchSO paymentSO = null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlPaymentAdvice paymentAdvice = getManagedBean().getPaymentAdvice();
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject("fl",IPaymentMatchSO.class);
			
			reqDTO.setPaymentAdvice(paymentAdvice);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getBpmTaskId());
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
}
