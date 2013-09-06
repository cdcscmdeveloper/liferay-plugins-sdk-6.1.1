package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sitevisit.SiteVisitAssignationManagedBean;

@ManagedBean(name = "siteVisitAssignatioBackingBean")
@RequestScoped
public class SiteVisitAssignationBackingBean extends BaseBackingBean<SiteVisitAssignationManagedBean> {
	public static Logger log = Logger.getLogger(SiteVisitAssignationManagedBean.class);
	
	public SiteVisitAssignationBackingBean() {
		
	}
}
