/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.common.supportdoc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Support Document
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "supportDocManagedBean")
@SessionScoped
public class SupportDocumentManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private SupportDocumentSearchBean supportDocSearchBean;

	/**
	 * @return the supportDocSearchBean
	 */
	public SupportDocumentSearchBean getSupportDocSearchBean() {
		return supportDocSearchBean;
	}

	/**
	 * @param supportDocSearchBean the supportDocSearchBean to set
	 */
	public void setSupportDocSearchBean(
			SupportDocumentSearchBean supportDocSearchBean) {
		this.supportDocSearchBean = supportDocSearchBean;
	}
	
	
	
}