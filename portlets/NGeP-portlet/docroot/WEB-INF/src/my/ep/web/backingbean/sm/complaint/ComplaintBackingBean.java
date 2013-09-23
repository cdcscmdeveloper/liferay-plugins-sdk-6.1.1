package my.ep.web.backingbean.sm.complaint;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.dto.sm.complaint.ComplaintReqDTO;
import my.ep.biz.dto.sm.complaint.ComplaintResDTO;
import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "complaintBackingBean")
@RequestScoped
public class ComplaintBackingBean extends BaseBackingBean<ComplaintManagedBean> {
	public static Logger log = Logger.getLogger(ComplaintManagedBean.class);

	public ComplaintBackingBean() {

	}

	/**
	 * Sample Skeleton to call SO -- Start
	 */

	public void lodgeComplaint() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.lodgeComplaint(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	public void sendForPtpkInitialReview() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.sendForPtpkInitialReview(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	public void updatePtpkInitialResponse() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.updatePtpkInitialResponse(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	public void sendForPtpkSecondReview() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.sendForPtpkSecondReview(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	public void updatePtpkSecondResponse() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.updatePtpkSecondResponse(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	public void sendForJkttFinalDecision() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.sendForJkttFinalDecision(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	public void imposeDisciplinaryAction() {
		IComplaintSO iComplaintSO = null;
		try {
			iComplaintSO = BusinessDelegate.getInstance().getServiceObject(
					IComplaintSO.class);
			ComplaintReqDTO complaintReqDTO = new ComplaintReqDTO();
			ComplaintResDTO complaintResDTO = iComplaintSO
					.imposeDisciplinaryAction(complaintReqDTO);
		} finally {
			iComplaintSO = null;
		}

	}

	/**
	 * Sample Skeleton to call SO -- End
	 */
}
