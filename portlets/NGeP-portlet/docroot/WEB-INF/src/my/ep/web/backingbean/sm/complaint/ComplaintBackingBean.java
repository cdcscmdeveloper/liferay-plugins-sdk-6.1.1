package my.ep.web.backingbean.sm.complaint;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.persistence.entity.sm.SmSupplier;
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

	public void proceedComplaint() {
		// TODO get the list of all suppliers from the DB later
		System.out.println(" ***** ENTER PRE RENDERED FOR COMPLAINT ***** ");
		List<SmSupplier> smSups = new ArrayList<SmSupplier>();
		SmSupplier sup1 = new SmSupplier();
		sup1.setSupplierId(1);
		sup1.setSupplierName("ABC Supplier");
		sup1.setRegNo("1234");
		smSups.add(sup1);
		SmSupplier sup2 = new SmSupplier();
		sup2.setSupplierId(2);
		sup2.setSupplierName("DEF Supplier");
		sup2.setRegNo("5678");
		smSups.add(sup2);
		SmSupplier sup3 = new SmSupplier();
		sup3.setSupplierId(3);
		sup3.setSupplierName("GHI Supplier");
		sup3.setRegNo("9012");
		smSups.add(sup3);

		getManagedBean().getSuppliersList().addAll(smSups);

	}

	public void onSupplierSelection() {
		System.out.println("***** ENTER THE SELECT ACTION ****");
		if (getManagedBean().getSelectedSupplier() == null) {
			System.out.println(" ***** NO VALUE IS SELECTES ****");
			getManagedBean().setSelectedSupplier(new SmSupplier());
		} else {
			System.out.println("**** SELECT ACTION ***"
					+ getManagedBean().getSelectedSupplier().getSupplierName());
		}
	}

	public void sample() {
		System.out.println(" **** SAMPLE ADD ********");
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
