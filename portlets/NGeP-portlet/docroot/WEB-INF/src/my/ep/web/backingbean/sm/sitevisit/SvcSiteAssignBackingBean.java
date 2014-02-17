package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.persistence.entity.sm.SmApplSiteVisit;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sitevisit.SvcSiteAssignManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	public static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	/*******Dummy start*****************/
	private DualListModel<String> officers;  

	public DualListModel<String> getOfficers() {
		return officers;
	}

	public void setOfficers(DualListModel<String> officers) {
		this.officers = officers;
	}
	/*******Dummy end*****************/
	
	public SvcSiteAssignBackingBean() {
		/*****************Dummy data**********************/
		 List<String> svOfficer = new ArrayList<String>(); 
		 List<String> conSvOfficer = new ArrayList<String>(); 
		 svOfficer.add("Mohammad Rizam");
		 svOfficer.add("Siti Nurul");
		 svOfficer.add("Mohammad Amin");
		 svOfficer.add("Haiza Aida");
		 svOfficer.add("Nurul Azlina");
		 
		 
		 officers = new DualListModel<String>(svOfficer, conSvOfficer);  
	}

	public void retrieveSiteAssignInfo() {
		//this.getManagedBean().setAppId(22);
		//this.getManagedBean().setSmApplSv(new SmApplSiteVisit());
		//this.getManagedBean().getSmApplSv().get
		
		//Old Version - Working set
		log.info("----------------------retrieveSiteAssignInfo start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteAssignSO.class);
		log.info("----------------------retrieveSiteAssignInfo----------------" + this.getManagedBean().getAppId());
		reqDTO.setAppId(this.getManagedBean().getAppId());
		//reqDTO.setAppId(2);
		SiteAssignResDTO resDTO = reqSO.retrieveSiteVisitByApplId(reqDTO);
		log.info("*********Address Size*************" + resDTO.getSmApplSv().getSmSiteVisitAddresses().size());

		this.getManagedBean().setSmApplSv(resDTO.getSmApplSv());
	}
	
	public String submitSiteVisitAssignment(String action){
		log.info("----------------------submitSiteVisitAssignment start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteAssignSO.class);
		String bpmUsrName = new FacesScopes().getUserScreenName();
		
		//reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setOutcome(action);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setBpmUserName(bpmUsrName);
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		
		SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);
		
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Site Visit has been submitted.", ""));
			return "/xhtml/sm/sitevisit/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}
	

	
}
