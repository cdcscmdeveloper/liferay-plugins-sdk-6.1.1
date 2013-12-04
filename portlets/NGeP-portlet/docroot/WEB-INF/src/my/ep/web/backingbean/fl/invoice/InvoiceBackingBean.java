/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.invoice;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.invoicepaymentdata.FLInvoiceAndPaymentDataType;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.invoice.InvoiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for invoice
 * 
 * @author KK LIM
 * @since 30/10/2013
 */

@ManagedBean(name = "invoiceBackingBean")
@RequestScoped
public class InvoiceBackingBean extends BaseBackingBean<InvoiceManagedBean> {
	private static Logger log = Logger.getLogger(InvoiceBackingBean.class);
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public InvoiceBackingBean(){
	}

	public String submitAction(String action) {
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		IInvoiceSO invoiceSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		try {

			FlInvoice invoice = getManagedBean().getInvoice();
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);

			reqDTO.setInvoice(invoice);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
			reqDTO.setDanRequired(getManagedBean().isDan());// for current testing DAN purpose in SO
			reqDTO.setUpdateDan(getManagedBean().isUpdateDan());
			reqDTO.setToken(token);
			FulfilmentResDTO resDTO = invoiceSO.saveInvoice(reqDTO);

			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "InvoiceNo. : "+invoice.getInvoiceNo();
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
			invoiceSO = null;
		}
		return null;
	}

	public void test(){
		System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
		System.out.println("getManagedBean().isUpdateDan(): "+getManagedBean().isUpdateDan());
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
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		FLInvoiceAndPaymentDataType paymentObj = (FLInvoiceAndPaymentDataType)obj;
		flReqDTO.setFlOrderId(paymentObj.getFullfillmentRequestId());
		IFulfilmentOrderSO flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);

		resDTO = flOrderSO.retrieveFulfilmentOrder(flReqDTO);
		//				resDTO = invoiceSO.retrieveInvoice(flReqDTO);//if save button are exist
		FlInvoice inv = new FlInvoice();
		inv.setFlFulfilmentOrder(resDTO.getFlOrder());

		//				InvoiceBackingBean invBB = new InvoiceBackingBean();
		getManagedBean().setInvoice(inv);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setOutComes(res.getAction());

		//		FacesContext fc = FacesContext.getCurrentInstance();
		//	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/fl/invoice/invBasePage.xhtml");
	}
}
