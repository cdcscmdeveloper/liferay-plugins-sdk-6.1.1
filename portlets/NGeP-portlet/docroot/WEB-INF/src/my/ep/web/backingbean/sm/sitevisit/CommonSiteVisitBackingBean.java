package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constants.SMProcessNameConst;
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
		
		Object token = new FacesScopes().getBPMAuthenticationCtxToken();
		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);
		
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);
		
		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO.getBPMTaskDetails(reqDTO);
		//SMDisciplinaryActionDataType disActObj = (SMDisciplinaryActionDataType) resDTO.getPojo();
		SMSiteVisitDataType svObj = (SMSiteVisitDataType) resDTO.getPojo();
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		
		log.info("----------Common SIte Visit--------" + resDTO.getTaskName());
		if(resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.ASSIGN_SITE_VISIT)){
			SvcSiteAssignBackingBean svcBB = new SvcSiteAssignBackingBean();
			svcBB.renewSesionObject();
			
			log.info("------Assign Site Visit AppID-------------" + svObj.getDocumentId());
			svcBB.getManagedBean().setAppId(svObj.getDocumentId());
			svcBB.getManagedBean().setBpmAction(resDTO.getAction());
			svcBB.getManagedBean().setTaskId(taskId);
			//svcBB.getManagedBean().setAppType(svObj.getAppType());
			svcBB.getManagedBean().setBpmPojo(svObj);
			svcBB.retrieveSiteAssignInfo(svObj.getDocumentId());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svcassignment/assignSiteVisitView");
		}else if(resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.COMPLETE_SV_REPORT)){
			log.info("------Complete Site Visit Report-------------" + svObj.getDocumentId());
			SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			svoBB.renewSesionObject();

			//svoBB.getManagedBean().setAppId(svObj.getDocumentId());
			svoBB.getManagedBean().setSvTaskId(Long.valueOf(svObj.getSvoTaskId()));
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
