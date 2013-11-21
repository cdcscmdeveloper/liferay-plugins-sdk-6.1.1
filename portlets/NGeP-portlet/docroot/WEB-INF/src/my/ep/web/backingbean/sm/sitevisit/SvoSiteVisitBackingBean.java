package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends
		BaseBackingBean<SvoSiteVisitManagedBean> {
	public static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);
//	public static String NaviToView = "siteVisitDetails?faces-redirect=true";
	//public static String NaviToSiteVisitTemplate = "submitOnlineRpt?faces-redirect=true";
	public static String NaviToSiteVisitTemplate = "submitOnlineRpt";
	public static String NaviToSiteVisitPreDetails = "submitOnlineRptPreDetails";
	public static String NaviToSiteVisitView = "siteVisitView";

	public SvoSiteVisitBackingBean() {

	}


	public String rptSubmitMethod() {
		log.info("-----------------rptSubmitMethod---------------"
				+ this.getManagedBean().getRptSubsMethod());
		if (this.getManagedBean().getRptSubsMethod().equals("Online")) {
			log.info("-----------------Navito template-----------");
			return NaviToSiteVisitTemplate;
		} else if (this.getManagedBean().getRptSubsMethod().equals("Upload")) {
			log.info("-----------------Navito template Upload-----------");
			return "siteVisitRptSubmission";
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

		FacesScopes fs = new FacesScopes();
		String naviParam = fs.getParameter("name1");

		log.info("----------siteVisitReviewNavi---------" + naviParam);
		//this.getManagedBean().setSvReviewNavi(naviParam);
	}

	public void retrieveSiteVisitInfo(){
		this.getManagedBean().setAppId(22);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setAppId(this.getManagedBean().getAppId());
		//reqDTO.setAppId(2);
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.retrieveSiteVisitInfoByAppId(reqDTO);
		this.getManagedBean().setSmApplSv(resDTO.getSmApplSv());
		log.info("----------retrieveSiteVisitInfo---------" + this.getManagedBean().getSmApplSv().getSmAppl().getSmSupplier().getSupplierName());
		
	}
	
	public String submitSiteVisitReport(String action){
		log.info("----------------------submitSiteVisitReport start----------------");
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setOutcome(action);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		SiteVisitResDTO resDTO = reqSO.submitSiteVisitReport(reqDTO);
		
		if(resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Sample info message", "PrimeFaces rocks!"));  
			return "taskListCommonPage";
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Sample Warning message", "PrimeFaces Sucks!")); 
			return "taskListCommonPage";
		}
	}
	
	public void uploadSiteVisitReport(){
		
	}
	
	public String naviToSiteVisitView(){
		return NaviToSiteVisitView;
	}
}
