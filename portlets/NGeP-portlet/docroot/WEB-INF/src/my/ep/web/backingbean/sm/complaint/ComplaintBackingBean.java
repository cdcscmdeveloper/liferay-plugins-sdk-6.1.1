package my.ep.web.backingbean.sm.complaint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmComplainant;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "complaintBackingBean")
@RequestScoped
public class ComplaintBackingBean extends BaseBackingBean<ComplaintManagedBean> {
	public static Logger log = Logger.getLogger(ComplaintManagedBean.class);

	public ComplaintBackingBean() {
		
		if (this.getManagedBean().getSmComplaint() == null) {
			log.info("--------------BackingBean is Null--------------------");

			this.getManagedBean().setSmComplaint(new SmComplaint());
//			this.getManagedBean().getSmComplaint()
//					.setSmComplainant(new SmComplainant());
/*			this.getManagedBean().setComplType(new HashMap<String, String>());
			this.getManagedBean().getComplType().put("R","Breach the obligation of registration");
			this.getManagedBean().getComplType().put("T","Breach the obligation of Tender/ Contract");
			this.getManagedBean().getComplType().put("C","Criminal or Civil Offenses");
			this.getManagedBean().getComplType().put("O","Others");*/
			//log.info("-----------Complaint Type----------------" + this.getManagedBean().getComplType().size());
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

//		this.getManagedBean().getSmComplaint().getSmComplainant()
//				.setPmCity(new PmCity());
//		this.getManagedBean().getSmComplaint().getSmComplainant()
//				.setPmCountry(new PmCountry());
//		this.getManagedBean().getSmComplaint().getSmComplainant()
//				.setPmState(new PmState());
//
//		this.getManagedBean().getSmComplaint().getSmComplainant().getPmCity()
//				.setCityId(1L);
//		this.getManagedBean().getSmComplaint().getSmComplainant().getPmState()
//				.setStateId(1L);
//		this.getManagedBean().getSmComplaint().getSmComplainant()
//				.getPmCountry().setCountryId(1);

		// String complNo = RandomStringUtils.random(20, false, true);
		String complNo = String.valueOf((new Date()).getTime());
		Date date = new Date();
		Timestamp currDate = new Timestamp(date.getTime());

		this.getManagedBean().getSmComplaint().setComplaintDate(currDate);
		this.getManagedBean().getSmComplaint().setComplaintNo(complNo);
//		this.getManagedBean().getSmComplaint().setStatusId(1);
		this.getManagedBean().getSmComplaint().setComplaintCategory("O");

//		this.getManagedBean().getSmComplaint().getSmComplainant()
//				.setSmComplaint(this.getManagedBean().getSmComplaint());

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
		// this.getManagedBean().setComplaintId(31);
		log.info("--------------retrieveComplaintInfo--------------------"
				+ this.getManagedBean().getComplaintId());
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		ComplaintResDTO resDTO = reqSO.retrieveComplaintInfoById(reqDTO);

		log.info("--------------statusId-------------------"
				+ resDTO.getSmComplaint().getStatusId());

		this.getManagedBean().setSmComplaint(resDTO.getSmComplaint());
		this.getManagedBean().setSmComplReview(new SmComplaintReview());
		this.getManagedBean().getSmComplaint().setSmComplaintReviews(new ArrayList<SmComplaintReview>());

		// this.getManagedBean().getSmComplaint().setSmComplaintReviews(new
		// ArrayList<SmComplaintReview>());
	}

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

//	public void onSupplierRowSelect() {
//		this.getManagedBean().getSmComplaint()
//				.setSmSupplier(this.getManagedBean().getSelectedSupplier());
//
//	}
//
	public String onComplaintRowSelect() {
		log.info("---------------onComplaintRowSelect------------------");
		this.getManagedBean().setComplaintId(
				this.getManagedBean().getSelectedComplaint().getComplaintId());
		this.retrieveComplaintInfo();
		return "complaintMain";
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
	

	public void retrieveAllActiveComplaint() {
		// SmComplaintReview bBean = this.getManagedBean().getSmComplReview();
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		ComplaintResDTO resDTO = reqSO.retrieveActiveComplaint(reqDTO);

		this.getManagedBean().setSmComplLs(resDTO.getSmComplLs());
	}

	public String updateComplaint() {
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		//long count = this.getManagedBean().getSmComplaint().getStatusId() + 1;
		
//		this.getManagedBean().getSmComplaint().setStatusId(this.getManagedBean().getSmComplaint().getStatusId() + 1);
//		long curStatus = this.getManagedBean().getSmComplaint().getStatusId();
//		if(curStatus == 29001){
//			this.getManagedBean().getSmComplaint().setStatusId(29002);
//			this.getManagedBean().getSmComplReview().setReviewCommittee("P1");
//			this.getManagedBean().getSmComplReview().setSmComplaint(this.getManagedBean().getSmComplaint());
//			this.getManagedBean().getSmComplaint().getSmComplaintReviews().add(this.getManagedBean().getSmComplReview());
//		}else if(curStatus == 29002){
//			this.getManagedBean().getSmComplaint().setStatusId(29003);
//			this.getManagedBean().getSmComplReview().setReviewCommittee("P1");
//			this.getManagedBean().getSmComplReview().setSmComplaint(this.getManagedBean().getSmComplaint());
//			this.getManagedBean().getSmComplaint().getSmComplaintReviews().add(this.getManagedBean().getSmComplReview());
//		}

		reqDTO.setSmComplaint(this.getManagedBean().getSmComplaint());
		ComplaintResDTO resDTO = reqSO.updateComplaint(reqDTO);
		
		return outputMsg(resDTO.isSuccessful(), "Complaint has been updated.");
	}

}
