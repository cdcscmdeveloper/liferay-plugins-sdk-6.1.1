/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.dan;

import java.text.ParseException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.dan.DebitAdjustmentNoteManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Managed Bean for Debit Adjustment Note
 * 
 * @author KK LIM
 * @since 1/11/2013
 *
 */
@ManagedBean(name = "danBackingBean")
@RequestScoped
public class DebitAdjustmentNoteBackingBean extends BaseBackingBean<DebitAdjustmentNoteManagedBean> {
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteBackingBean.class);
	
	public String submitAction(String action) throws ParseException {
		System.out.println("--DAN submit--");
		IAdjustmentSO adjustmentSO = null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlAdjustment adjustment = getManagedBean().getAdjustment();
			adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl", IAdjustmentSO.class);
			reqDTO.setAdjustment(adjustment);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getBpmTaskId());
			FulfilmentResDTO resDTO = adjustmentSO.saveAdjustment(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Adjustment Id. : "+adjustment.getAdjustmentId();
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
			adjustmentSO = null;
		}
		return null;
	}
}
