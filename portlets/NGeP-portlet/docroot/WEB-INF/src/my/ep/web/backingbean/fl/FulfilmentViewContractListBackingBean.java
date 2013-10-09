/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.fl.IFulfilmentSO;
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

@ManagedBean(name = "fulfilmentViewContractListBackingBean")
@RequestScoped
public class FulfilmentViewContractListBackingBean extends BaseBackingBean<FulfilmentViewContractListManagedBean> {
	private static Logger log = Logger.getLogger(FulfilmentViewContractListBackingBean.class);
	
	public void retrieveContractList(){
		IFulfilmentSO iFulfilmentSO=null;
		try{
			iFulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(IFulfilmentSO.class);
			List<FulfilmentViewContractListManagedBean>contractList=iFulfilmentSO.retrieveContractList();
		}
		catch(Exception e){
			
		}
	}

}
