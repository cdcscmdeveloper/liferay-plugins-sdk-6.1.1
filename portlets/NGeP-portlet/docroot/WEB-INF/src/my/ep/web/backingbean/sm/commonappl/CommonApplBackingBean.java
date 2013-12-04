package my.ep.web.backingbean.sm.commonappl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.managedbean.sm.commonappl.CommonApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "commApplBackingBean")
@RequestScoped
public class CommonApplBackingBean extends BaseBackingBean<CommonApplManagedBean> {
	public static Logger log = Logger.getLogger(CommonApplBackingBean.class);
	
	public void preRenderView() {
		log.info("-------ComAppl prerender view-------");
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		
		String bpmUserName = new FacesScopes().getUserScreenName();
		
		reqDTO.setTaskId(taskId);
		reqDTO.setBpmUserName(bpmUserName);
		
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		
		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO.getBPMTaskDetails(reqDTO);
	
		SMMOFRegDataType smMofObj= (SMMOFRegDataType) resDTO.getPojo();
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		
		if(resDTO.getTaskName().equalsIgnoreCase("Verify Supporting Documents")){
			SdoReviewBackingBean sdoBB = new SdoReviewBackingBean();
			
			log.info("------Verify Supporting Documents-----------" + smMofObj.getAppId());
			sdoBB.getManagedBean().setAppId(
					(Long.parseLong(smMofObj.getAppId())));
			sdoBB.getManagedBean().setBpmAction(resDTO.getAction());
			sdoBB.getManagedBean().setTaskId(taskId);
			sdoBB.viewSupportingDocument();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/sdoreview/reviewSupportingDocuments");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Make Payment")){
			SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			svoBB.getManagedBean().setAppId(
					(Long.parseLong(smMofObj.getAppId())));
			svoBB.getManagedBean().setBpmAction(resDTO.getAction());
			svoBB.getManagedBean().setTaskId(taskId);
			
//			FacesContext fc = FacesContext.getCurrentInstance();
//			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svositevisit/siteVisitView");
		}
		
	}
	
//	public void preRender(){
//		log.info("------ preRender-------");
//		HttpServletRequest servletRequest = PortalUtil
//				.getHttpServletRequest(new FacesScopes().getRequest());
//		HttpServletRequest originalServletRequest = PortalUtil
//				.getOriginalServletRequest(servletRequest);
//		String taskId = originalServletRequest.getParameter("taskId");
//		String loginId = originalServletRequest.getParameter("loginId");
//		
//		//log.info("--------------------" + taskId);
//		log.info("----------managedBean----------" + this.getManagedBean().getTaskId());
//		if(taskId != null){
//			this.getManagedBean().setTaskId(taskId);
//		}
//		
//		//this.getManagedBean().setTaskId(taskId);
//	}
}