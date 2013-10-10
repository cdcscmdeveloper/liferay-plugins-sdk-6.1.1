package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sitevisit.SiteVisitAssignationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "siteVisitAssignatioBackingBean")
@RequestScoped
public class SiteVisitAssignationBackingBean extends
		BaseBackingBean<SiteVisitAssignationManagedBean> {
	public static Logger log = Logger
			.getLogger(SiteVisitAssignationManagedBean.class);

	public SiteVisitAssignationBackingBean() {
	}

	public void retrieveSiteVisitSchedule() {
		ISiteVisitSO iSiteVisitSO = null;
		try {
			iSiteVisitSO = BusinessDelegate.getInstance().getServiceObject(
					ISiteVisitSO.class);
			SiteVisitReqDTO siteVisitReqDTO = new SiteVisitReqDTO();
			SiteVisitResDTO siteVisitResDTO = iSiteVisitSO
					.downloadSVTemplateByType(siteVisitReqDTO);
		} finally {
			iSiteVisitSO = null;
		}

	}

	public void assignSiteVisitOfficers() {
		ISiteVisitSO iSiteVisitSO = null;
		try {
			iSiteVisitSO = BusinessDelegate.getInstance().getServiceObject(
					ISiteVisitSO.class);
			SiteVisitReqDTO siteVisitReqDTO = new SiteVisitReqDTO();
			SiteVisitResDTO siteVisitResDTO = iSiteVisitSO
					.downloadSVTemplateByType(siteVisitReqDTO);
		} finally {
			iSiteVisitSO = null;
		}

	}

	public void findSiteVisitDetailsById() {
		ISiteVisitSO iSiteVisitSO = null;
		try {
			iSiteVisitSO = BusinessDelegate.getInstance().getServiceObject(
					ISiteVisitSO.class);
			SiteVisitReqDTO siteVisitReqDTO = new SiteVisitReqDTO();
			SiteVisitResDTO siteVisitResDTO = iSiteVisitSO
					.downloadSVTemplateByType(siteVisitReqDTO);
		} finally {
			iSiteVisitSO = null;
		}

	}
}
