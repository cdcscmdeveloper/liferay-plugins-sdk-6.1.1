/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.onlinebiddingstatus;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class OnlineBiddingStatusCountManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean
@SessionScoped
public class OnlineBiddingStatusCountManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(OnlineBiddingStatusCountManagedBean.class);
	
	private List<PmStatusDesc> statusDescList;

	/**
	 * @return the statusDescList
	 */
	public List<PmStatusDesc> getStatusDescList() {
		return statusDescList;
	}

	/**
	 * @param statusDescList the statusDescList to set
	 */
	public void setStatusDescList(List<PmStatusDesc> statusDescList) {
		this.statusDescList = statusDescList;
	}

}
