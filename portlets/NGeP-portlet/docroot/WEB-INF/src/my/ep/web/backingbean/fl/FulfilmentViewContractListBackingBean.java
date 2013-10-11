/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.FulfilmentViewContractListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean for Search Contract Order List
 * 
 * @author Giribabu
 * @since 4/10/2013
 */

@ManagedBean(name ="fulfilmentViewContractListBackingBean")
@SessionScoped
public class FulfilmentViewContractListBackingBean extends BaseBackingBean<FulfilmentViewContractListManagedBean> {
	
	private static Logger log = Logger.getLogger(FulfilmentViewContractListBackingBean.class);
	
	public FulfilmentViewContractListBackingBean(){
		
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		ContractResDTO resDto = fulfilmentRequestSO.retrieveContractList();
		List<CtContract> contractList = resDto.getCtContracts();
		log.info("Remarks List Size in Backing Bean:::::>>>>::: "+contractList.size());
		this.getManagedBean().setContractList(contractList);
	}
}
