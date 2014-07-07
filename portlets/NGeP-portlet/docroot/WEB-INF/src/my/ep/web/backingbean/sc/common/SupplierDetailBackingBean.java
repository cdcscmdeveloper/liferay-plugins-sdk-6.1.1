/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.common.SupplierDetailManagedBean;

import org.apache.log4j.Logger;

/**
 * Common Supplier Detail Backing Bean
 * 
 * @author Kenny
 * @since 05.07.2014
 */
@ManagedBean
@RequestScoped
public class SupplierDetailBackingBean extends SourcingBaseBackingBean<SupplierDetailManagedBean> {
	public static Logger log = EpLogger.getLogger(SupplierDetailBackingBean.class);

	public SupplierDetailBackingBean() {
	}
}
