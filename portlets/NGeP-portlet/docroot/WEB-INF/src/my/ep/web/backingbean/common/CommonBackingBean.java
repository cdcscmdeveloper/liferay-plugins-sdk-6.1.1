/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.common;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.common.CommonManagedBean;

import org.apache.log4j.Logger;

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
		try {
			generateReport(getManagedBean().getReportName(), getManagedBean().getParamMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
