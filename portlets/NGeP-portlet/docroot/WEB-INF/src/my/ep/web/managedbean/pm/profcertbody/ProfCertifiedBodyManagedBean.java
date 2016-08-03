/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.profcertbody;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.web.bean.pm.ProfCertifiedBodySearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Professional Certificate Body
 * 
 * @author DK
 * @since 21/07/2014
 */
@ManagedBean(name = "profcertifiedbodyManagedBean")
@SessionScoped
public class ProfCertifiedBodyManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private ProfCertifiedBodySearchBean profCertifiedBodySearchBean = new ProfCertifiedBodySearchBean();

	/**
	 * Search Bean List
	 */
	private List<PmCertBody> certBodyList;

	private PmCertBody certBody;

	private boolean islazy = false;
	
	private PmCertBody oldPmCertBody;

	public ProfCertifiedBodyManagedBean(){}
	
	/**
	 * @return the certBodyList
	 */
	public List<PmCertBody> getCertBodyList() {
		return certBodyList;
	}

	/**
	 * @param certBodyList
	 *            the certBodyList to set
	 */
	public void setCertBodyList(List<PmCertBody> certBodyList) {
		this.certBodyList = certBodyList;
	}

	/**
	 * @return the certBody
	 */
	public PmCertBody getCertBody() {
		return certBody;
	}

	/**
	 * @param certBody
	 *            the certBody to set
	 */
	public void setCertBody(PmCertBody certBody) {
		this.certBody = certBody;
	}

	/**
	 * @return the oldPmCertBody
	 */
	public PmCertBody getOldPmCertBody() {
		return oldPmCertBody;
	}

	/**
	 * @param oldPmCertBody the oldPmCertBody to set
	 */
	public void setOldPmCertBody(PmCertBody oldPmCertBody) {
		this.oldPmCertBody = oldPmCertBody;
	}

	/**
	 * @return the islazy
	 */
	public boolean isIslazy() {
		return islazy;
	}

	/**
	 * @param islazy the islazy to set
	 */
	public void setIslazy(boolean islazy) {
		this.islazy = islazy;
	}

	/**
	 * @return the profCertifiedBodySearchBean
	 */
	public ProfCertifiedBodySearchBean getProfCertifiedBodySearchBean() {
		return profCertifiedBodySearchBean;
	}

	/**
	 * @param profCertifiedBodySearchBean the profCertifiedBodySearchBean to set
	 */
	public void setProfCertifiedBodySearchBean(
			ProfCertifiedBodySearchBean profCertifiedBodySearchBean) {
		this.profCertifiedBodySearchBean = profCertifiedBodySearchBean;
	}

}
