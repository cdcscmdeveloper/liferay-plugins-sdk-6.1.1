/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.invoice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.invoice.InvoiceManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
	
	public InvoiceBackingBean(){
	}
	
	public void submitAction(String action) {
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
			reqDTO.setTaskId(getManagedBean().getBpmTaskId());
			System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
			reqDTO.setDanRequired(getManagedBean().isDan());// for current testing DAN purpose in SO
			reqDTO.setUpdateDan(getManagedBean().isUpdateDan());
			invoiceSO.saveInvoice(reqDTO);

		}catch(Exception e){
			log.error("Exception>>", e);
		}finally {
			invoiceSO = null;
		}
	}
	
	public void test(){
		System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
		System.out.println("getManagedBean().isUpdateDan(): "+getManagedBean().isUpdateDan());
	}
}
