package my.ep.web.backingbean.sm.sdoreview;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewResDTO;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sdoreview.SdoReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "sdoReviewBackingBean")
@RequestScoped
public class SdoReviewBackingBean extends BaseBackingBean<SdoReviewManagedBean> {

	public static Logger log = Logger.getLogger(SdoReviewBackingBean.class);

	public SdoReviewBackingBean() {

	}

	public void viewSupportingDocument() {

		//this.getManagedBean().setAppId(22);
		log.info("----------appID------------"
				+ this.getManagedBean().getAppId());

		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);
		reqDTO.setApplId(this.getManagedBean().getAppId());
		SdoReviewResDTO resDTO = reqSO.retrieveApplById(reqDTO);

		this.getManagedBean().setSmAppl(resDTO.getSmAppl());
		log.info("----------------------supportingDocMode-------------------------"
				+ resDTO.getSmAppl().getSupportingDocMode());

	}

	public String submitSDOReviewAction(String res) {

		/*
		 * if (res.equalsIgnoreCase("VERIFIED")) { log.info(
		 * "----------------------submitSDOReviewAction-------------------------"
		 * + res);
		 */
		String bpmUsrName = new FacesScopes().getUserScreenName();


		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);
//		reqDTO.setApplId(this.getManagedBean().getAppId());
		reqDTO.setOutcome(res);
//		reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
//				.getSupplierId());
		//reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		
		System.out.println("-----------SDO----------" + reqDTO.getOutcome());
		System.out.println("-----------SDO Review----------" + reqDTO.getTaskId());

		reqDTO.setBpmUserName(bpmUsrName);
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());

		SdoReviewResDTO resDTO = reqSO.submitBPMSdoReview(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Application has been updated successfully", ""));
			return "/xhtml/sm/disciplinaryAction/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
		// }
	}

	public void preRenderView() {
		log.info("--------------SDO preRenderView--------------");
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String bpmUsrName = new FacesScopes().getUserScreenName();
		
		log.info("----------------BPMUSER-------------------" + bpmUsrName);
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		reqDTO.setTaskId(taskId);
		reqDTO.setBpmUserName(bpmUsrName);

		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);

		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO
				.getBPMTaskDetails(reqDTO);
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		this.getManagedBean().setBpmAction(resDTO.getAction());
		this.getManagedBean().setTaskId(taskId);
		
		//Old BPM working set
		SMMOFRegDataType mofObj = (SMMOFRegDataType) resDTO.getPojo();
		log.info("--------------mofObj.getAppId()--------------------"+mofObj.getDocumentId());
		this.getManagedBean().setAppId(mofObj.getDocumentId());
		//this.getManagedBean().setBpmPojo(mofObj);
		viewSupportingDocument();

		// if(resDTO.getTaskName().equalsIgnoreCase("Update Complaint")){
		// FacesContext fc = FacesContext.getCurrentInstance();
		// fc.getApplication().getNavigationHandler().handleNavigation(fc, null,
		// "complaintFirstReviewInfo");
		// }else
		// if(resDTO.getTaskName().equalsIgnoreCase("Input PTPK Response")){
		//
		// FacesContext fc = FacesContext.getCurrentInstance();
		// fc.getApplication().getNavigationHandler().handleNavigation(fc, null,
		// "updateComplaintFirstReview");
		//
		// }
		//
		// log.info("------------BpmResponseDTO TASKNAME-----------------" +
		// resDTO.getTaskName());
	}
}
