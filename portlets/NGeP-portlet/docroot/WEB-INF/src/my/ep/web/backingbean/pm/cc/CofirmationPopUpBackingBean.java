/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */

package my.ep.web.backingbean.pm.cc;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.cc.ConfirmationPopUpManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class CofirmationPopUpBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "cofirmationPopUpBackingBean")
@ViewScoped
public class CofirmationPopUpBackingBean extends
		BaseBackingBean<ConfirmationPopUpManagedBean> {
	public static Logger log = EpLogger
			.getLogger(CofirmationPopUpBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	public CofirmationPopUpBackingBean() {
	}

	public void okAction(String methodActionStr) {
		debugLogger("okAction");

		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes.createMethodExpression(
					FacesContext.getCurrentInstance(), methodActionStr, null,
					new Class[] {});

			methodExp.invoke(FacesContext.getCurrentInstance().getELContext(),
					new Object[] {});

		}
	}
}
