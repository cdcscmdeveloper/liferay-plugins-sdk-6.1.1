/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.common.rejectionreason;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.common.rejectionreason.RejectionReasonManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
/**
 * Backing Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */

@ManagedBean(name="rejectionReasonBackingBean")
@ViewScoped
public class RejectionReasonBackingBean extends BaseBackingBean<RejectionReasonManagedBean>{
	private static Logger log = Logger.getLogger(RejectionReasonBackingBean.class);

//	/**
//	 * get faces scopes
//	 * @return FacesScopes
//	 */
//	private FacesScopes getFacesScopes(){
//		FacesScopes facesScopes = new FacesScopes();
//		return facesScopes;
//	}
}
