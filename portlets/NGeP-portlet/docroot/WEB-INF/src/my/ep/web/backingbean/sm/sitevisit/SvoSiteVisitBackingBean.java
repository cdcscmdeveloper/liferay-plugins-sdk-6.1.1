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


	public void selectSubmissionMethod() {
		log.info("-----------rptSubmitMethod current--------------" + this.getManagedBean().getRptSubsMethod());
		log.info("-----------rptSubmitMethod--------------");
		this.getManagedBean().setRptSubsMethod(this.getManagedBean().getRptSubsMethod());
		log.info("-----------rptSubmitMethod newer--------------" + this.getManagedBean().getRptSubsMethod());
		//this.getManagedBean().setRptSubsMethod(this.getManagedBean().getRptSubsMethod());
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
		//this.getManagedBean().setAppId(22);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setAppId(this.getManagedBean().getAppId());
		//reqDTO.setAppId(2);
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.retrieveSiteVisitInfoByAppId(reqDTO);
		this.getManagedBean().setSmApplSv(resDTO.getSmApplSv());
		this.getManagedBean().setRptSubsMethod("Online");
		//log.info("----------retrieveSiteVisitInfo---------" + this.getManagedBean().getSmApplSv().getSmAppl().getSmSupplier().getSupplierName());
		
	}
	
	public void nextTab(){
		
	}
	
	public String submitSiteVisitReport(String action){
		/*Map map2 = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String action = (String) map2.get("name1");
		*/
		log.info("----------------------submitSiteVisitReport start----------------" +  action);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		
		String bpmUsrName = new FacesScopes().getUserScreenName();
		
		reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setOutcome(action);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setBpmUserName(bpmUsrName);
		//reqDTO.setAppType(this.getManagedBean().getAppType());
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		SiteVisitResDTO resDTO = reqSO.submitSiteVisitReport(reqDTO);
		
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Site Visit Report has been submitted.", ""));
			return "/xhtml/sm/sitevisit/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
		
	}
	
	public void uploadSiteVisitReport(){
		log.info("-----------uploadSiteVisitReport--------------");
	}
	
	public String naviToSiteVisitView(){
		return NaviToSiteVisitView;
	}
	
	public void changeRptSubsMethod(){
		
		log.info("----------Report Subs Method-------------" + this.getManagedBean().getRptSubsMethod());
	}
	
}
