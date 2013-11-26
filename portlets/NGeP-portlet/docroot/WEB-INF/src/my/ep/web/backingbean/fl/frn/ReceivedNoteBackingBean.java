/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.frn;

import java.text.ParseException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.frn.ReceivedNoteManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean for Create Received Note 
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "recievedNoteBackingBean")
@RequestScoped
public class ReceivedNoteBackingBean extends BaseBackingBean<ReceivedNoteManagedBean> {
	private static Logger log = Logger.getLogger(ReceivedNoteBackingBean.class);

	public String submitAction(String action) throws ParseException {
		IReceivedNoteSO receivedNoteSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentNote fulfilmentNote = getManagedBean().getFulfilmentNote();
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject("fl",IReceivedNoteSO.class);
			reqDTO.setFulfilmentNote(fulfilmentNote);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setDeliveryOrderId(getManagedBean().getDeliveryOrderId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			FulfilmentResDTO resDTO = receivedNoteSO.saveReceivedNote(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "fulfilmentNote id. : "+fulfilmentNote.getFulfilmentNoteId();
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
		
		} finally {
			receivedNoteSO = null;
		}
		return null;
	}
}
