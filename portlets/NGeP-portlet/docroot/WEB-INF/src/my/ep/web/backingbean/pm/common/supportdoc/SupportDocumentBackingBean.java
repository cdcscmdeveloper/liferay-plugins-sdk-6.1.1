package my.ep.web.backingbean.pm.common.supportdoc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

import org.apache.log4j.Logger;

/**
 * Backing Bean for Support Document
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name="supportDocBackingBean")
@ViewScoped
public class SupportDocumentBackingBean extends BaseBackingBean<SupportDocumentManagedBean>{
	private static Logger log = Logger.getLogger(SupportDocumentBackingBean.class);

}
