/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.common.CommonManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Backing Bean For Contract Details
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="commonBackingBean")
@RequestScoped
public class CommonBackingBean extends BaseBackingBean<CommonManagedBean> {

	private static Logger log = Logger.getLogger(CommonBackingBean.class);
	
	public void generateReport() {
		log.debug("generateReport() : "+getManagedBean().getReportDisplayName());
		String reportUrl = generateReportURL(getManagedBean().getReportName(), getManagedBean().getParamMap());
		String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/ReportViewServlet";		
		
		PortletRequest portalRequest = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletRequest req = com.liferay.portal.util.PortalUtil.getOriginalServletRequest(com.liferay.portal.util.PortalUtil.getHttpServletRequest(portalRequest));
		req.getSession().setAttribute("generatedUrl", reportUrl);
		RequestContext.getCurrentInstance().addCallbackParam("reportViewUrl", path);
	}
	
}
