package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.commonappl.CommonApplBackingBean;
import my.ep.web.managedbean.sm.commonappl.CommonApplManagedBean;
import my.ep.web.managedbean.sm.sitevisit.CommonSiteVisitManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "commSiteVisitBackingBean")
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
		String loginId = originalServletRequest.getParameter("loginId");		
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		
		reqDTO.setTaskId(taskId);

		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		
		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO.getBPMTaskDetails(reqDTO);
		//SMDisciplinaryActionDataType disActObj = (SMDisciplinaryActionDataType) resDTO.getPojo();
		SMSiteVisitDataType svObj = (SMSiteVisitDataType) resDTO.getPojo();
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		
		if(resDTO.getTaskName().equalsIgnoreCase("Assign Site Visit")){
			SvcSiteAssignBackingBean svcBB = new SvcSiteAssignBackingBean();
			
			log.info("------Assign Site Visit AppID-------------" + svObj.getAppId());
			svcBB.getManagedBean().setAppId(
					(Long.parseLong(svObj.getAppId())));
			svcBB.getManagedBean().setBpmAction(resDTO.getAction());
			svcBB.getManagedBean().setTaskId(taskId);
			svcBB.retrieveSiteAssignInfo();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svcassignment/assignSiteVisitView");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Complete Site Visit Report")){
			SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			svoBB.getManagedBean().setAppId(
					(Long.parseLong(svObj.getAppId())));
			svoBB.getManagedBean().setBpmAction(resDTO.getAction());
			svoBB.getManagedBean().setTaskId(taskId);
			svoBB.retrieveSiteVisitInfo();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svositevisit/siteVisitView");
		}
		
	}
}
