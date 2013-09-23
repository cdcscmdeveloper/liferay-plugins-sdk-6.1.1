package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.FacesScopes;

@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends
		BaseBackingBean<SvoSiteVisitManagedBean> {
	public static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);
	public static String NaviToView = "siteVisitDetails?faces-redirect=true";
	public static String NaviToSiteVisitTemplate = "submitOnlineRpt?faces-redirect=true";
	public static String NaviToSiteVisitPreDetails = "submitOnlineRptPreDetails";

	public SvoSiteVisitBackingBean() {
		
	}

	public String proceedToView() {
		return NaviToView;
	}

	public String rptSubmitMethod() {
		log.info("-----------------rptSubmitMethod---------------"
				+ this.getManagedBean().getRptSubsMethod());
		if (this.getManagedBean().getRptSubsMethod().equals("Online")) {
			log.info("-----------------Navito template-----------");
			return NaviToSiteVisitTemplate;
		} else if (this.getManagedBean().getRptSubsMethod().equals("Upload")) {
			return "siteVisitRptSubmission?faces-redirect=true";
		} else {
			return "siteVisitRptSubmission";
		}
	}

	public String naviToSiteVisitTemplate() {
		return NaviToSiteVisitTemplate;
	}

	public String naviToSiteVisitPreDetails() {
		log.info("----------naviToSiteVisitPreDetails---------");

		return NaviToSiteVisitPreDetails;
	}

	public void siteVisitReviewNavi() {
		log.info("----------siteVisitReviewNavi---------");
		// String naviParam =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("name");
		// Map map2 =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		// String naviParam = (String) map2.get("name1");

		FacesScopes fs = new FacesScopes();
		String naviParam = fs.getParameter("name1");

		log.info("----------siteVisitReviewNavi---------" + naviParam);
		this.getManagedBean().setSvReviewNavi(naviParam);
	}
}
