package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sitevisit.CommonSiteVisitManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "commonSiteVisitBackingBean")
@RequestScoped
public class CommonSiteVisitBackingBean extends BaseBackingBean<CommonSiteVisitManagedBean>{
	public static Logger log = Logger.getLogger(CommonSiteVisitBackingBean.class);

	public void preRenderView() {
		log.info("-------ComAppl prerender view-------");
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		//String bpmUsrName = new FacesScopes().getUserScreenName();
		
		String token = new FacesScopes().getBPMAuthenticationCtxToken();
		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);
		
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);
		
		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO.getBPMTaskDetails(reqDTO);
		//SMDisciplinaryActionDataType disActObj = (SMDisciplinaryActionDataType) resDTO.getPojo();
		SMSiteVisitDataType svObj = (SMSiteVisitDataType) resDTO.getPojo();
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		
		log.info("----------Common SIte Visit--------" + resDTO.getTaskName());
		if(resDTO.getTaskName().equalsIgnoreCase("Assign Site Visit")){
			SvcSiteAssignBackingBean svcBB = new SvcSiteAssignBackingBean();
			
			log.info("------Assign Site Visit AppID-------------" + svObj.getDocumentId());
			svcBB.getManagedBean().setAppId(svObj.getDocumentId());
			svcBB.getManagedBean().setBpmAction(resDTO.getAction());
			svcBB.getManagedBean().setTaskId(taskId);
			//svcBB.getManagedBean().setAppType(svObj.getAppType());
			svcBB.getManagedBean().setBpmPojo(svObj);
			svcBB.retrieveSiteAssignInfo();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svcassignment/assignSiteVisitView");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Complete Site Visit Report")){
			log.info("------Complete Site Visit Report-------------" + svObj.getDocumentId());
			SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			svoBB.getManagedBean().setAppId(svObj.getDocumentId());
			svoBB.getManagedBean().setBpmAction(resDTO.getAction());
			svoBB.getManagedBean().setTaskId(taskId);
			//svoBB.getManagedBean().setAppType(svObj.getAppType());
			svoBB.getManagedBean().setBpmPojo(svObj);
			svoBB.retrieveSiteVisitInfo("");
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svositevisit/siteVisitMain");
		}
		
	}
}
