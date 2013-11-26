package my.ep.web.backingbean.sm.complaint;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.jaxb.sm.disciplinaryactiondata.SMDisciplinaryActionDataType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmComplainant;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "complaintBackingBean")
@RequestScoped
public class ComplaintBackingBean extends BaseBackingBean<ComplaintManagedBean> {
	public static Logger log = Logger.getLogger(ComplaintManagedBean.class);

	public ComplaintBackingBean() {
		log.info("--------------"+this.getManagedBean().getTaskName()+"------------");
		if (this.getManagedBean().getSmComplaint() == null) {
			log.info("--------------BackingBean is Null--------------------");

			this.getManagedBean().setSmComplaint(new SmComplaint());
			this.getManagedBean().getSmComplaint()
					.setSmComplainant(new SmComplainant());

			// this.getManagedBean().setPmCity(new PmCity());
			// this.getManagedBean().setPmCountry(new PmCountry());
			// this.getManagedBean().setPmState(new PmState());
			// this.getManagedBean().getSmComplaint().getSmComplainant()
			// .setPmCity(new PmCity());
			// this.getManagedBean().getSmComplaint().getSmComplainant()
			// .setPmCountry(new PmCountry());
			// this.getManagedBean().getSmComplaint().getSmComplainant()
			// .setPmState(new PmState());

		}
	}

	public String lodgeComplaint() {
		log.info("-----------------");
		// this.getManagedBean().getSmComplaint().getSmComplainant().setPmCity(this.getManagedBean().getPmCity());
		// this.getManagedBean().getSmComplaint().getSmComplainant().setPmCountry(this.getManagedBean().getPmCountry());
		// this.getManagedBean().getSmComplaint().getSmComplainant().setPmState(this.getManagedBean().getPmState());

		this.getManagedBean().getSmComplaint().getSmComplainant()
				.setPmCity(new PmCity());
		this.getManagedBean().getSmComplaint().getSmComplainant()
				.setPmCountry(new PmCountry());
		this.getManagedBean().getSmComplaint().getSmComplainant()
				.setPmState(new PmState());

		this.getManagedBean().getSmComplaint().getSmComplainant().getPmCity()
				.setCityId(1L);
		this.getManagedBean().getSmComplaint().getSmComplainant().getPmState()
				.setStateId(1L);
		this.getManagedBean().getSmComplaint().getSmComplainant()
				.getPmCountry().setCountryId(1);

		String complNo = RandomStringUtils.random(20, false, true);
		this.getManagedBean().getSmComplaint().setComplaintNo(complNo);

		this.getManagedBean().getSmComplaint().getSmComplainant()
				.setSmComplaint(this.getManagedBean().getSmComplaint());

		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		reqDTO.setSmComplaint(this.getManagedBean().getSmComplaint());
		ComplaintResDTO resDTO = reqSO.lodgeComplaint(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Your complaint has been submitted successfully",
							""));
			return "/xhtml/sm/complaint/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Something Error", ""));
			return "lodgeComplaint";
		}
	}

	public void retrieveComplaintInfo() {
		//this.getManagedBean().setComplaintId(31);
		log.info("--------------retrieveComplaintInfo--------------------"
				+ this.getManagedBean().getComplaintId());
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		ComplaintResDTO resDTO = reqSO.retrieveComplaintInfoById(reqDTO);

		this.getManagedBean().setSmComplaint(resDTO.getSmComplaint());
		this.getManagedBean().setSmComplReview(new SmComplaintReview());

		// this.getManagedBean().getSmComplaint().setSmComplaintReviews(new
		// ArrayList<SmComplaintReview>());
	}

	public String sendForPtpkInitialReview() {
		log.info("-----------------------------sendForPtpkInitialReview-----------");
		
		log.info("-------------------sendForPtpkInitialReview---------------------" + this.getManagedBean().getTaskId());

		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		this.getManagedBean().getSmComplReview()
				.setSmComplaint(this.getManagedBean().getSmComplaint());
		this.getManagedBean().getSmComplReview().setReviewCommittee("P1");

		reqDTO.setSmComplReview(this.getManagedBean().getSmComplReview());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setOutcome("SUBMIT");

		ComplaintResDTO resDTO = reqSO.updateReview(reqDTO);

		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	public String updatePtpkInitialResponse() {
		SmComplaintReview bBean = this.getManagedBean().getSmComplReview();

		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);

		if (bBean.getDecisionTaken().equals("1")) {
			log.info("----------------------IS VALID-----------"
					+ bBean.getDecisionTaken());
			reqDTO.setOutcome("VALID");
		} else {
			log.info("----------------------INVALID-----------"
					+ bBean.getDecisionTaken());
			reqDTO.setOutcome("INVALID");
		}

		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		bBean.setReviewCommittee("P1");
		bBean.setRoundNo(1);
		bBean.setSmComplaint(this.getManagedBean().getSmComplaint());
		bBean.setConveneDate(this.getManagedBean().getSmComplReview()
				.getActualConveneDate());

		reqDTO.setSmComplReview(bBean);
		ComplaintResDTO resDTO = reqSO.updateReview(reqDTO);

		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	// public String updateShowCauseLetterIssuance(String outcome) {
	// SmComplaintReview bBean = this.getManagedBean().getSmComplReview();
	//
	// ComplaintReqDTO reqDTO = new ComplaintReqDTO();
	// IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
	// .getServiceObject("sm", IComplaintSO.class);
	// reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
	// reqDTO.setOutcome(outcome);
	// reqDTO.setTaskId(this.getManagedBean().getTaskId());
	//
	// ComplaintResDTO resDTO = reqSO.updateShowCauseLetterInfo(reqDTO);
	//
	// return outputMsg(resDTO.isSuccessful(),
	// "Complaint has been updated successfully");
	//
	// // reqDTO.setTaskId(this.getManagedBean().getTaskId());
	// // bBean.setReviewCommittee("P1");
	// // bBean.setConveneDate(date);
	// // bBean.setSmComplaint(this.getManagedBean().getSmComplaint());
	// //
	// bBean.setConveneDate(this.getManagedBean().getSmComplReview().getActualConveneDate());
	// }

	public void searchSupplier() {
		log.info("--------searchSupplier-------");
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		reqDTO.setSupplierId(3);
		reqDTO.setSupplierName("MDEC malaysia");
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		ComplaintResDTO resDTO = reqSO.retrieveSupplier(reqDTO);
		this.getManagedBean().setSmSupplierLs(resDTO.getSmSuppLs());
	}

	public void onSupplierRowSelect() {
		this.getManagedBean().getSmComplaint()
				.setSmSupplier(this.getManagedBean().getSelectedSupplier());

	}

	public String updateLetterResponse(String outcome) {
		log.info("-------------------updateShowCauseLetterResponse-----------"
				+ outcome);
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		reqDTO.setOutcome(outcome);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());

		// reqSO.updateShowCauseLetterInfo(reqDTO);

		ComplaintResDTO resDTO = reqSO.updateShowCauseLetterInfo(reqDTO);

		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	public String updateComplaintSecReviewInfo(String outcome) {
		log.info("-------------------updateComplaintSecReviewInfo-----------"
				+ outcome);
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		reqDTO.setOutcome(outcome);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());

		ComplaintResDTO resDTO = reqSO.updateComplaintSecondReview(reqDTO);

		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	public String updateComplaintSecReviewResponse() {
//
//		log.info("-------------------updateComplaintSecReviewResponse-----------"
//				+ outcome);
		SmComplaintReview bBean = this.getManagedBean().getSmComplReview();
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);

		if (bBean.getDecisionTaken().equals("1")) {
			reqDTO.setOutcome("VALID");
		} else {
			reqDTO.setOutcome("INVALID");
		}
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());

		ComplaintResDTO resDTO = reqSO.updateComplaintSecondReview(reqDTO);
		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	public String updateLetterIssuance(String outcome) {
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		reqDTO.setOutcome(outcome);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());

		ComplaintResDTO resDTO = reqSO.updateShowCauseLetterInfo(reqDTO);

		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	public String submitJKTTFinalDecision() {
		SmComplaintReview bBean = this.getManagedBean().getSmComplReview();
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		if (bBean.getDecisionTaken().equals("1")
				|| bBean.getDecisionTaken().equals("2")) {
			reqDTO.setOutcome("VALID");
		} else {
			reqDTO.setOutcome("INVALID");
		}
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		ComplaintResDTO resDTO = reqSO.submitJKTTFinalDecision(reqDTO);

		return outputMsg(resDTO.isSuccessful(),
				"Complaint has been updated successfully");
	}

	public String outputMsg(boolean succ, String msg) {
		if (succ) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, msg, ""));
			return "/xhtml/sm/disciplinaryAction/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}

	public void preRenderView() {
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
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		this.getManagedBean().setBpmAction(resDTO.getAction());
		this.getManagedBean().setTaskId(taskId);
		
		log.info("------------resDTO.getTaskId()-------------------" + resDTO.getTaskId());
		log.info("-------------complaint taskName-----------" + resDTO.getTaskName());
 
		
		SMDisciplinaryActionDataType disActObj = (SMDisciplinaryActionDataType) resDTO.getPojo();
		log.info("-------------complaint id-----------" + disActObj.getComplainId());
		this.getManagedBean().setComplaintId(Long.parseLong(disActObj.getComplainId()));
		
		if(resDTO.getTaskName().equalsIgnoreCase("Update Complaint")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "complaintFirstReviewInfo");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Input PTPK Response")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "updateComplaintFirstReview");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Prepare And Issue Showcause Letter")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "updateShowCauseLetterInfo");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Update Supplier Response")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "updateShowCauseLetterResponse");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Present To PTPK Second Review")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "complaintSecondReviewInfo");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Input PTPK Case Outcome")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "updateComplaintSecondReview");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Prepare And Issue Accusation Letter")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "updateAccusationLetterInfo");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Review Supplier Response And Consideration Paper")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "updateAccusationLetterResponse");
		}else if(resDTO.getTaskName().equalsIgnoreCase("Input JKTT Decision")){
			retrieveComplaintInfo();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "JKTTDecision");
		}
		
		
		
		
		//log.info("------------BpmResponseDTO TASKNAME-----------------" + resDTO.getTaskName());
	}
}
