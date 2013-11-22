/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.shippingInstr;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.fl.request.IShippingInstructionSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.shippingInstr.ShippingInstrManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean for Shipping Instruction
 * 
 * @author KK LIM
 * @since 28/10/2013
 */

@ManagedBean(name = "shipInstrBackingBean")
@RequestScoped
public class ShippingInstrBackingBean extends BaseBackingBean<ShippingInstrManagedBean>{
	private static Logger log = Logger.getLogger(ShippingInstrBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";


	public ShippingInstrBackingBean(){

	}
	
	public void submitShippingAction(String action){
		System.out.println("-- shipping Instr --");
		IShippingInstructionSO shippingInstrSO = null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlShippingInstr shippingInstr = getManagedBean().getShippingInstr();
			shippingInstrSO = (IShippingInstructionSO) BusinessDelegate.getInstance().getServiceObject("fl",IShippingInstructionSO.class);
//			reqDTO.setFlFulfilmentRequest(getManagedBean().getFlRequest());
			reqDTO.setShippingInstr(shippingInstr);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getBpmTaskId());
			if(getManagedBean().getTempLogin() == null){
				reqDTO.setTempLogin("roslan");
			}else{
				reqDTO.setTempLogin(getManagedBean().getTempLogin());
			}
			shippingInstrSO.saveShippingInstruction(reqDTO);

		}catch(Exception e){
			log.error("Exception>>", e);
		}finally {
			shippingInstrSO = null;
		}
	}


}
