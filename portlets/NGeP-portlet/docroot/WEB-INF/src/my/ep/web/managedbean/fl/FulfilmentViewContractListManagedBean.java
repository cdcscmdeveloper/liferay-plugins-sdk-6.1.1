/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Search Contract Order List
 * 
 * @author Giribabu
 * @since 4/10/2013
 */
@ManagedBean
@SessionScoped
public class FulfilmentViewContractListManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(FulfilmentViewContractListManagedBean.class);
	private static final long serialVersionUID = 1L;
	
	private List <FulfilmentViewContractListManagedBean> contractList;

	/**
	 * @return the contractList
	 */
	public List<FulfilmentViewContractListManagedBean> getContractList() {
		return contractList;
	}

	/**
	 * @param contractList the contractList to set
	 */
	public void setContractList(
			List<FulfilmentViewContractListManagedBean> contractList) {
		this.contractList = contractList;
	}

}
