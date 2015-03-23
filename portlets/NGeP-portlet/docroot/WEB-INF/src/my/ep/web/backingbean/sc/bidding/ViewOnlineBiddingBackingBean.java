/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.bidding;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sc.QTConstants;

/**
 * Online Bidding View Backing Bean
 * 
 * @author Kenny
 * @since 19.10.2014
 */
@ManagedBean(name = "viewOnlineBiddingBackingBean")
@RequestScoped
public class ViewOnlineBiddingBackingBean extends BaseBackingBean {

	public ViewOnlineBiddingBackingBean() {
	}

	/**
	 * To redirect to Supplier online Bidding Screen..
	 */
	public void preRenderView() {

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.ONLINE_BIDDING_MAIN_PAGE);
	}
}
